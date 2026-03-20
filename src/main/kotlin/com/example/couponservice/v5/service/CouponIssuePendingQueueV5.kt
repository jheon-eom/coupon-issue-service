package com.example.couponservice.v5.service

import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.data.redis.core.script.DefaultRedisScript
import org.springframework.stereotype.Component

@Component
class CouponIssuePendingQueueV5(
    private val stringRedisTemplate: StringRedisTemplate,
    @Value("\${coupon.v5.redis.pending-key:coupon:v5:pending}")
    private val pendingKey: String,
    @Value("\${coupon.v5.redis.processing-key:coupon:v5:processing}")
    private val processingKey: String,
    @Value("\${coupon.v5.redis.retry-key:coupon:v5:retry}")
    private val retryKey: String,
    @Value("\${coupon.v5.redis.relay-failed-key:coupon:v5:relay:failed}")
    private val relayFailedKey: String
) {
    private val reserveAndEnqueueScript = DefaultRedisScript<Long>().apply {
        setScriptText(
            """
            local current = redis.call('INCR', KEYS[1])
            local limit = tonumber(ARGV[1])
            if current > limit then
              redis.call('DECR', KEYS[1])
              return 0
            end
            redis.call('LPUSH', KEYS[2], ARGV[2])
            return 1
            """.trimIndent()
        )
        resultType = Long::class.java
    }

    fun reserveAndEnqueue(couponId: Long, issueLimit: Int, envelope: String): Boolean {
        val result = stringRedisTemplate.execute(
            reserveAndEnqueueScript,
            listOf(counterKey(couponId), pendingKey),
            issueLimit.toString(),
            envelope
        ) ?: 0L

        return result == 1L
    }

    fun moveDueRetriesToPending(limit: Int, nowEpochMillis: Long): Int {
        val due = stringRedisTemplate.opsForZSet().rangeByScore(retryKey, 0.0, nowEpochMillis.toDouble(), 0, limit.toLong())
            ?: emptySet()

        var moved = 0
        for (item in due) {
            val removed = stringRedisTemplate.opsForZSet().remove(retryKey, item) ?: 0
            if (removed > 0) {
                stringRedisTemplate.opsForList().leftPush(pendingKey, item)
                moved++
            }
        }

        return moved
    }

    fun recoverProcessingToPending(limit: Int): Int {
        var recovered = 0
        repeat(limit) {
            val moved = stringRedisTemplate.opsForList().rightPopAndLeftPush(processingKey, pendingKey)
            if (moved == null) {
                return@repeat
            }
            recovered++
        }
        return recovered
    }

    fun popToProcessing(): String? {
        return stringRedisTemplate.opsForList().rightPopAndLeftPush(pendingKey, processingKey)
    }

    fun acknowledge(envelope: String) {
        stringRedisTemplate.opsForList().remove(processingKey, 1, envelope)
    }

    fun scheduleRetry(envelope: String, nextEpochMillis: Long) {
        stringRedisTemplate.opsForZSet().add(retryKey, envelope, nextEpochMillis.toDouble())
    }

    fun putRelayFailed(envelope: String, reason: String?) {
        val sanitizedReason = (reason ?: "unknown").replace("\n", " ")
        stringRedisTemplate.opsForList().leftPush(relayFailedKey, "$sanitizedReason|$envelope")
    }

    fun counterKey(couponId: Long): String {
        return "coupon:$couponId:issued"
    }
}
