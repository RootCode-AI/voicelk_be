package com.voicelk.voicelk_be.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.voicelk.voicelk_be.dto.AnswerDto;
import com.voicelk.voicelk_be.dto.AudioDto;
import com.voicelk.voicelk_be.dto.DownloadLogDto;
import com.voicelk.voicelk_be.dto.GuestUserDto;
import com.voicelk.voicelk_be.dto.QueryDto;
import com.voicelk.voicelk_be.dto.RegisteredUserDto;
import com.voicelk.voicelk_be.dto.UserDto;
import com.voicelk.voicelk_be.dto.UserFeedbackDto;
import com.voicelk.voicelk_be.entity.Answer;
import com.voicelk.voicelk_be.entity.Audio;
import com.voicelk.voicelk_be.entity.DownloadLog;
import com.voicelk.voicelk_be.entity.GuestUser;
import com.voicelk.voicelk_be.entity.Query;
import com.voicelk.voicelk_be.entity.RegisteredUser;
import com.voicelk.voicelk_be.entity.User;
import com.voicelk.voicelk_be.entity.UserFeedback;

/**
 * Converts entities to DTOs, replacing related entities with their id fields
 * so that bidirectional JPA associations never reach Jackson.
 */
@Component
public class EntityMapper {

    public UserDto toDto(User user) {
        if (user == null) {
            return null;
        }
        return new UserDto(user.getUserId(), user.getRole());
    }

    public List<UserDto> toUserDtoList(List<User> users) {
        return users.stream().map(this::toDto).collect(Collectors.toList());
    }

    public RegisteredUserDto toDto(RegisteredUser user) {
        if (user == null) {
            return null;
        }
        return new RegisteredUserDto(
                user.getUserId(),
                user.getRole(),
                user.getUserName(),
                user.getEmail(),
                user.getAccountStatus(),
                user.getFailedLoginCount(),
                user.getLockTimestamp(),
                user.getFirebaseUid(),
                user.getProfilePicture(),
                user.getAuthProvider());
    }

    public List<RegisteredUserDto> toRegisteredUserDtoList(List<RegisteredUser> users) {
        return users.stream().map(this::toDto).collect(Collectors.toList());
    }

    public GuestUserDto toDto(GuestUser user) {
        if (user == null) {
            return null;
        }
        return new GuestUserDto(user.getUserId(), user.getRole(), user.getSessionId(), user.getIpAddress());
    }

    public List<GuestUserDto> toGuestUserDtoList(List<GuestUser> users) {
        return users.stream().map(this::toDto).collect(Collectors.toList());
    }

    public QueryDto toDto(Query query) {
        if (query == null) {
            return null;
        }
        return new QueryDto(
                query.getQueryId(),
                query.getInputText(),
                query.getTimestamp(),
                query.getSyllabusTopic(),
                query.getUser() != null ? query.getUser().getUserId() : null,
                query.getAnswer() != null ? query.getAnswer().getAnswerId() : null);
    }

    public List<QueryDto> toQueryDtoList(List<Query> queries) {
        return queries.stream().map(this::toDto).collect(Collectors.toList());
    }

    public AnswerDto toDto(Answer answer) {
        if (answer == null) {
            return null;
        }
        return new AnswerDto(
                answer.getAnswerId(),
                answer.getResponseText(),
                answer.getSource(),
                answer.getQuery() != null ? answer.getQuery().getQueryId() : null);
    }

    public List<AnswerDto> toAnswerDtoList(List<Answer> answers) {
        return answers.stream().map(this::toDto).collect(Collectors.toList());
    }

    public AudioDto toDto(Audio audio) {
        if (audio == null) {
            return null;
        }
        List<String> downloadLogIds = audio.getDownloadLogs() == null
                ? List.of()
                : audio.getDownloadLogs().stream().map(DownloadLog::getLogId).collect(Collectors.toList());
        return new AudioDto(
                audio.getAudioId(),
                audio.getFilePath(),
                audio.getFormat(),
                audio.getDuration(),
                audio.getModelVersion(),
                audio.getProcessingTime(),
                audio.getAnswer() != null ? audio.getAnswer().getAnswerId() : null,
                downloadLogIds,
                audio.getUserFeedback() != null ? audio.getUserFeedback().getFeedbackId() : null);
    }

    public List<AudioDto> toAudioDtoList(List<Audio> audios) {
        return audios.stream().map(this::toDto).collect(Collectors.toList());
    }

    public DownloadLogDto toDto(DownloadLog log) {
        if (log == null) {
            return null;
        }
        return new DownloadLogDto(
                log.getLogId(),
                log.getDate(),
                log.getUser() != null ? log.getUser().getUserId() : null,
                log.getAudio() != null ? log.getAudio().getAudioId() : null);
    }

    public List<DownloadLogDto> toDownloadLogDtoList(List<DownloadLog> logs) {
        return logs.stream().map(this::toDto).collect(Collectors.toList());
    }

    public UserFeedbackDto toDto(UserFeedback feedback) {
        if (feedback == null) {
            return null;
        }
        return new UserFeedbackDto(
                feedback.getFeedbackId(),
                feedback.getRating(),
                feedback.getComment(),
                feedback.getTimestamp(),
                feedback.getRegisteredUser() != null ? feedback.getRegisteredUser().getUserId() : null,
                feedback.getAudio() != null ? feedback.getAudio().getAudioId() : null);
    }

    public List<UserFeedbackDto> toUserFeedbackDtoList(List<UserFeedback> feedbacks) {
        return feedbacks.stream().map(this::toDto).collect(Collectors.toList());
    }
}
