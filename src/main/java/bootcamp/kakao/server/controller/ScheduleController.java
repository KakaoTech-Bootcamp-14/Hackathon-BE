package bootcamp.kakao.server.controller;

import bootcamp.kakao.server.common.dto.DataResponseDto;
import bootcamp.kakao.server.common.enums.Code;
import bootcamp.kakao.server.dto.schedule.CreateScheduleRequestDto;
import bootcamp.kakao.server.dto.schedule.CreateScheduleResponseDto;
import bootcamp.kakao.server.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping("/api/schedule")
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public DataResponseDto<CreateScheduleResponseDto> createSchedule(
            @RequestPart("multipartFile") MultipartFile multipartFile,
            @RequestPart("request") CreateScheduleRequestDto request
    ){
        CreateScheduleResponseDto createScheduleResponseDto = scheduleService.createSchedule(multipartFile, request);
        return new DataResponseDto<>(Code.OK, "학습 스케줄 생성이 성공적으로 완료되었습니다.", createScheduleResponseDto);
    }

    @PostMapping("/reschedule")
    public DataResponseDto<CreateScheduleResponseDto> reCreateSchedule(
            @RequestBody CreateScheduleRequestDto request
    ) {
        CreateScheduleResponseDto createScheduleResponseDto = scheduleService.reCreateSchedule(request);
        return new DataResponseDto<>(Code.OK, "학습 스케줄 재생성이 성공적으로 완료되었습니다.", createScheduleResponseDto);
    }
}
