package com.keimons.platform.log;

import ch.qos.logback.classic.LoggerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * System.out和System.err重定向到Logback
 *
 * @author monkey1993
 * @version 1.0
 * @date 2019-09-17
 * @since 1.0
 */
public enum SystemLogger {

	/**
	 * 输出日志 System.out
	 */
	INFO {
		@Override
		public void log(final String message) {
			LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
			Logger logger = context.getLogger(SystemPrint.OUT_CONSOLE);
			logger.info(message);
		}
	},

	/**
	 * 错误日志 System.err
	 */
	ERROR {
		@Override
		public void log(final String message) {
			LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
			Logger logger = context.getLogger(SystemPrint.ERR_CONSOLE);
			logger.error(message);
		}
	};

	/**
	 * 输出日志
	 *
	 * @param message 信息
	 */
	public abstract void log(String message);
}