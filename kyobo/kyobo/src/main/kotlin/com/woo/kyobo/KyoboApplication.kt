package com.woo.kyobo

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class KyoboApplication

fun main(args: Array<String>) {
	runApplication<KyoboApplication>(*args)
}
