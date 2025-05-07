package smu.capstone.intrastructure.chatting.config;

import lombok.RequiredArgsConstructor;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import smu.capstone.domain.chat.exception.AsyncExceptionHandler;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

@EnableAsync
@RequiredArgsConstructor
@Configuration
public class AsyncConfig implements AsyncConfigurer {

    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);        // 기본 스레드 수
        executor.setMaxPoolSize(30);        // 최대 스레드 수
        executor.setQueueCapacity(500);     // 대기열 큐 크기
        executor.setThreadNamePrefix("AsyncExecutor-");     //스레드 prifix 커스텀
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());    //스레드 거절될 시 호출한 곳에서 처리
        executor.initialize();
        return executor;
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return new AsyncExceptionHandler();
    }
}