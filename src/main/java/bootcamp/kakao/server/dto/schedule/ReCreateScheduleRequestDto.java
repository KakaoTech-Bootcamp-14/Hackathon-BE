package bootcamp.kakao.server.dto.schedule;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ReCreateScheduleRequestDto {

    @JsonProperty("study_session_id")
    private String learningSourceId;

    @JsonProperty("remaining_days")
    private int remainingDays;

    @JsonProperty("hours_per_day")
    private float dailyStudyTime;

    @JsonProperty("studied_topics")
    private List<String> completedTaskTitles;

    @JsonProperty("pending_topics")
    private List<String> remainingTaskTitles;
}
