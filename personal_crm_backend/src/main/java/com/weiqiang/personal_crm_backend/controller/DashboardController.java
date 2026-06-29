package com.weiqiang.personal_crm_backend.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.weiqiang.personal_crm_backend.common.Constants;
import com.weiqiang.personal_crm_backend.common.ErrorCode;
import com.weiqiang.personal_crm_backend.common.Result;
import com.weiqiang.personal_crm_backend.entity.Contact;
import com.weiqiang.personal_crm_backend.entity.Todo;
import com.weiqiang.personal_crm_backend.exception.BusinessException;
import com.weiqiang.personal_crm_backend.enums.RelationshipHealthStatus;
import com.weiqiang.personal_crm_backend.mapper.ActivityLogMapper;
import com.weiqiang.personal_crm_backend.mapper.ContactMapper;
import com.weiqiang.personal_crm_backend.mapper.TodoMapper;
import com.weiqiang.personal_crm_backend.model.dto.ContactLatestActivityDTO;
import com.weiqiang.personal_crm_backend.model.vo.ContactHealthItemVO;
import com.weiqiang.personal_crm_backend.model.vo.ContactGenderDistributionVO;
import com.weiqiang.personal_crm_backend.model.vo.DashboardOverviewVO;
import com.weiqiang.personal_crm_backend.model.vo.RelationshipHealthVO;
import com.weiqiang.personal_crm_backend.model.vo.TodoTrendVO;
import com.weiqiang.personal_crm_backend.security.UserContext;
import com.weiqiang.personal_crm_backend.service.ContactHealthCalculator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Controller for dashboard statistics
 */
@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
@Slf4j
public class DashboardController {

    private final ContactMapper contactMapper;
    private final TodoMapper todoMapper;
    private final ActivityLogMapper activityLogMapper;
    private final ContactHealthCalculator contactHealthCalculator;
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    /**
     * Get dashboard overview metrics
     */
    @GetMapping("/overview")
    public Result<DashboardOverviewVO> getOverview() {
        String userId = UserContext.getUserId();
        if (userId == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }

        String cacheKey = Constants.REDIS_KEY_DASHBOARD_CACHE + userId;
        String field = "overview";
        try {
            String cachedJson = (String) redisTemplate.opsForHash().get(cacheKey, field);
            if (cachedJson != null && !cachedJson.trim().isEmpty()) {
                DashboardOverviewVO vo = objectMapper.readValue(cachedJson, DashboardOverviewVO.class);
                return Result.success(vo);
            }
        } catch (Exception e) {
            log.warn("Failed to read overview cache from Redis for user: {}", userId, e);
        }

        DashboardOverviewVO overview = new DashboardOverviewVO();

        // 1. contactCount (status = 0)
        Long contactCount = contactMapper.selectCount(
                new LambdaQueryWrapper<Contact>()
                        .eq(Contact::getUserId, userId)
                        .eq(Contact::getStatus, 0)
        );
        overview.setContactCount(contactCount);

        // 2. blacklistCount (status = 1)
        Long blacklistCount = contactMapper.selectCount(
                new LambdaQueryWrapper<Contact>()
                        .eq(Contact::getUserId, userId)
                        .eq(Contact::getStatus, 1)
        );
        overview.setBlacklistCount(blacklistCount);

        // 3. pendingTodoCount (status = 0)
        Long pendingTodoCount = todoMapper.selectCount(
                new LambdaQueryWrapper<Todo>()
                        .eq(Todo::getUserId, userId)
                        .eq(Todo::getStatus, 0)
        );
        overview.setPendingTodoCount(pendingTodoCount);

        // 4. todayTodoCount (00:00 - 23:59:59, status = 0)
        LocalDateTime startOfToday = LocalDate.now().atStartOfDay();
        LocalDateTime endOfToday = LocalDate.now().atTime(LocalTime.MAX);
        Long todayTodoCount = todoMapper.selectCount(
                new LambdaQueryWrapper<Todo>()
                        .eq(Todo::getUserId, userId)
                        .eq(Todo::getStatus, 0)
                        .ge(Todo::getTodoTime, startOfToday)
                        .le(Todo::getTodoTime, endOfToday)
        );
        overview.setTodayTodoCount(todayTodoCount);

        // 5. overdueTodoCount (before now, status = 0)
        Long overdueTodoCount = todoMapper.selectCount(
                new LambdaQueryWrapper<Todo>()
                        .eq(Todo::getUserId, userId)
                        .eq(Todo::getStatus, 0)
                        .lt(Todo::getTodoTime, LocalDateTime.now())
        );
        overview.setOverdueTodoCount(overdueTodoCount);

        try {
            String json = objectMapper.writeValueAsString(overview);
            redisTemplate.opsForHash().put(cacheKey, field, json);
            redisTemplate.expire(cacheKey, Constants.DASHBOARD_CACHE_TTL_SECONDS, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("Failed to write overview cache to Redis for user: {}", userId, e);
        }

        return Result.success(overview);
    }

    /**
     * Get todo trend for future days
     */
    @GetMapping("/todo-trend")
    public Result<List<TodoTrendVO>> getTodoTrend(@RequestParam(value = "days", defaultValue = "7") Integer days) {
        String userId = UserContext.getUserId();
        if (userId == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }

        String cacheKey = Constants.REDIS_KEY_DASHBOARD_CACHE + userId;
        String field = "todo-trend:" + days;
        try {
            String cachedJson = (String) redisTemplate.opsForHash().get(cacheKey, field);
            if (cachedJson != null && !cachedJson.trim().isEmpty()) {
                List<TodoTrendVO> voList = objectMapper.readValue(
                        cachedJson, 
                        objectMapper.getTypeFactory().constructCollectionType(List.class, TodoTrendVO.class)
                );
                return Result.success(voList);
            }
        } catch (Exception e) {
            log.warn("Failed to read todo-trend cache from Redis for user: {}", userId, e);
        }

        List<TodoTrendVO> trendList = new ArrayList<>();
        LocalDate today = LocalDate.now();
        for (int i = 0; i < days; i++) {
            LocalDate targetDate = today.plusDays(i);
            LocalDateTime start = targetDate.atStartOfDay();
            LocalDateTime end = targetDate.atTime(LocalTime.MAX);

            Long count = todoMapper.selectCount(
                    new LambdaQueryWrapper<Todo>()
                            .eq(Todo::getUserId, userId)
                            .eq(Todo::getStatus, 0)
                            .ge(Todo::getTodoTime, start)
                            .le(Todo::getTodoTime, end)
            );

            trendList.add(new TodoTrendVO(targetDate.toString(), count));
        }

        try {
            String json = objectMapper.writeValueAsString(trendList);
            redisTemplate.opsForHash().put(cacheKey, field, json);
            redisTemplate.expire(cacheKey, Constants.DASHBOARD_CACHE_TTL_SECONDS, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("Failed to write todo-trend cache to Redis for user: {}", userId, e);
        }

        return Result.success(trendList);
    }

    /**
     * Get contact gender distribution (status = 0)
     */
    @GetMapping("/contact-gender-distribution")
    public Result<List<ContactGenderDistributionVO>> getContactGenderDistribution() {
        String userId = UserContext.getUserId();
        if (userId == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }

        String cacheKey = Constants.REDIS_KEY_DASHBOARD_CACHE + userId;
        String field = "contact-gender-distribution";
        try {
            String cachedJson = (String) redisTemplate.opsForHash().get(cacheKey, field);
            if (cachedJson != null && !cachedJson.trim().isEmpty()) {
                List<ContactGenderDistributionVO> voList = objectMapper.readValue(
                        cachedJson, 
                        objectMapper.getTypeFactory().constructCollectionType(List.class, ContactGenderDistributionVO.class)
                );
                return Result.success(voList);
            }
        } catch (Exception e) {
            log.warn("Failed to read gender distribution cache from Redis for user: {}", userId, e);
        }

        List<ContactGenderDistributionVO> list = new ArrayList<>();

        Long unknownCount = contactMapper.selectCount(
                new LambdaQueryWrapper<Contact>()
                        .eq(Contact::getUserId, userId)
                        .eq(Contact::getStatus, 0)
                        .eq(Contact::getGender, 0)
        );
        Long maleCount = contactMapper.selectCount(
                new LambdaQueryWrapper<Contact>()
                        .eq(Contact::getUserId, userId)
                        .eq(Contact::getStatus, 0)
                        .eq(Contact::getGender, 1)
        );
        Long femaleCount = contactMapper.selectCount(
                new LambdaQueryWrapper<Contact>()
                        .eq(Contact::getUserId, userId)
                        .eq(Contact::getStatus, 0)
                        .eq(Contact::getGender, 2)
        );

        // Unicode escaped: "\u672a\u77e5" = "未知", "\u7537" = "男", "\u5973" = "女"
        list.add(new ContactGenderDistributionVO(0, "\u672a\u77e5", unknownCount));
        list.add(new ContactGenderDistributionVO(1, "\u7537", maleCount));
        list.add(new ContactGenderDistributionVO(2, "\u5973", femaleCount));

        try {
            String json = objectMapper.writeValueAsString(list);
            redisTemplate.opsForHash().put(cacheKey, field, json);
            redisTemplate.expire(cacheKey, Constants.DASHBOARD_CACHE_TTL_SECONDS, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("Failed to write gender distribution cache to Redis for user: {}", userId, e);
        }

        return Result.success(list);
    }

    /**
     * Get contact relationship health status distribution
     */
    @GetMapping("/relationship-health")
    public Result<RelationshipHealthVO> getRelationshipHealth() {
        String userId = UserContext.getUserId();
        if (userId == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }

        String cacheKey = Constants.REDIS_KEY_DASHBOARD_CACHE + userId;
        String field = "relationship-health";
        try {
            String cachedJson = (String) redisTemplate.opsForHash().get(cacheKey, field);
            if (cachedJson != null && !cachedJson.trim().isEmpty()) {
                RelationshipHealthVO vo = objectMapper.readValue(cachedJson, RelationshipHealthVO.class);
                return Result.success(vo);
            }
        } catch (Exception e) {
            log.warn("Failed to read relationship health cache from Redis for user: {}", userId, e);
        }

        // 1. Get all active contacts (status = 0) for current user
        List<Contact> contacts = contactMapper.selectList(
                new LambdaQueryWrapper<Contact>()
                        .eq(Contact::getUserId, userId)
                        .eq(Contact::getStatus, 0)
        );

        if (contacts.isEmpty()) {
            RelationshipHealthVO emptyVo = new RelationshipHealthVO(0L, 0L, 0L, 0L, 0L);
            try {
                String json = objectMapper.writeValueAsString(emptyVo);
                redisTemplate.opsForHash().put(cacheKey, field, json);
                redisTemplate.expire(cacheKey, Constants.DASHBOARD_CACHE_TTL_SECONDS, TimeUnit.SECONDS);
            } catch (Exception e) {
                log.error("Failed to write relationship health cache to Redis for user: {}", userId, e);
            }
            return Result.success(emptyVo);
        }

        // 2. Query latest activity timestamp and title for user's contacts in batch
        List<ContactLatestActivityDTO> activityList = activityLogMapper.selectLatestActivityByUserId(userId);
        Map<String, ContactLatestActivityDTO> latestActivityMap = activityList.stream()
                .filter(a -> a.getContactId() != null && a.getLatestActivityTime() != null)
                .collect(Collectors.toMap(ContactLatestActivityDTO::getContactId, a -> a, (k1, k2) -> k1));

        // 3. Evaluate each contact's health status via calculator and collect details
        List<ContactHealthItemVO> activeList = new ArrayList<>();
        List<ContactHealthItemVO> followUpList = new ArrayList<>();
        List<ContactHealthItemVO> inactiveList = new ArrayList<>();
        List<ContactHealthItemVO> noActivityList = new ArrayList<>();

        LocalDate today = LocalDate.now();

        for (Contact contact : contacts) {
            ContactLatestActivityDTO dto = latestActivityMap.get(contact.getCtId());
            LocalDateTime latestTime = dto != null ? dto.getLatestActivityTime() : null;
            RelationshipHealthStatus status = contactHealthCalculator.calculateStatus(contact, latestTime);

            Long daysAgo = null;
            String lastEventTitle = dto != null ? dto.getLastEventTitle() : null;
            if (latestTime != null) {
                long diff = ChronoUnit.DAYS.between(latestTime.toLocalDate(), today);
                daysAgo = diff < 0 ? 0L : diff;
            }

            ContactHealthItemVO item = new ContactHealthItemVO(
                    contact.getCtId(),
                    contact.getName(),
                    contact.getRemarks(), // avatarUrl/remarks fallback
                    daysAgo,
                    lastEventTitle
            );

            switch (status) {
                case ACTIVE:
                    activeList.add(item);
                    break;
                case FOLLOW_UP:
                    followUpList.add(item);
                    break;
                case INACTIVE:
                    inactiveList.add(item);
                    break;
                case NO_ACTIVITY:
                    noActivityList.add(item);
                    break;
            }
        }

        RelationshipHealthVO vo = new RelationshipHealthVO(
                (long) activeList.size(),
                (long) followUpList.size(),
                (long) inactiveList.size(),
                (long) noActivityList.size(),
                (long) contacts.size()
        );
        vo.setActiveList(activeList);
        vo.setFollowUpList(followUpList);
        vo.setInactiveList(inactiveList);
        vo.setNoActivityList(noActivityList);

        try {
            String json = objectMapper.writeValueAsString(vo);
            redisTemplate.opsForHash().put(cacheKey, field, json);
            redisTemplate.expire(cacheKey, Constants.DASHBOARD_CACHE_TTL_SECONDS, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("Failed to write relationship health cache to Redis for user: {}", userId, e);
        }

        return Result.success(vo);
    }
}
