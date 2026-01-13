package com.example.smartschedule.aspect;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
@RequiredArgsConstructor
public class LoggingAspect {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final HttpServletRequest request; // Inject HttpServletRequest

    @Before("execution(* com.example.smartschedule.service.*.*(..))")
    public void logBefore(JoinPoint joinPoint) {
        String clientIp = getClientIP();
        logger.info("IP: {} - Entering method: {} with arguments: {}",
                clientIp,
                joinPoint.getSignature().getName(),
                Arrays.toString(joinPoint.getArgs()));
    }

    @AfterReturning(pointcut = "execution(* com.example.smartschedule.service.*.*(..))", returning = "result")
    public void logAfter(JoinPoint joinPoint, Object result) {
        String clientIp = getClientIP();
        logger.info("IP: {} - Exiting method: {} with result: {}",
                clientIp,
                joinPoint.getSignature().getName(),
                result);
    }

    // طريقة استخراج IP العميل بشكل آمن حتى لو التطبيق خلف Proxy
    private String getClientIP() {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null) {
            return request.getRemoteAddr();
        }
        // إذا كان هناك أكثر من IP مفصول بفواصل، خذ الأول
        return xfHeader.split(",")[0];
    }
}
