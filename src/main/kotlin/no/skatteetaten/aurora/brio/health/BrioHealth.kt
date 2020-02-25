package no.skatteetaten.aurora.brio.health

import org.springframework.boot.actuate.health.Health
import org.springframework.boot.actuate.health.HealthIndicator

class BrioHealth : HealthIndicator {

    override fun health(): Health {
        return Health.up()
                .build()
/*
        return if (currentValue % 2 == 0L) {
            Health.status("OBSERVE")
                    .withDetail("message", "Even number in nominator")
                    .withDetail("Count", currentValue)
                    .build()
        } else {
            Health.up()
                .withDetail("Count", currentValue)
                .build()
        }
*/
    }
}
