package com.capstone.usa.chat.service;

import com.capstone.usa.chat.dto.ChatRoomIdDto;
import com.capstone.usa.chat.model.Chat;
import com.capstone.usa.chat.model.ChatRoom;
import com.capstone.usa.chat.repository.ChatRepository;
import com.capstone.usa.chat.repository.ChatRoomRepository;
import com.capstone.usa.jobpost.model.Job;
import com.capstone.usa.jobpost.service.JobService;
import com.capstone.usa.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatRepository chatRepository;
    private final JobService jobService;

    @Transactional
    public ResponseEntity<?> createChatRoom(int jobId, User currentUser) {
        Job job = jobService.findJobById(jobId);
        if (job == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("해당 게시물을 찾을 수 없습니다");
        }

        User postOwner = job.getUser();

        if (currentUser.getId().equals(job.getUser().getId())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("자신이 작성한 게시물에 대한 채팅은 할 수 없습니다");
        }

        Optional<ChatRoom> optionalChatRoom = chatRoomRepository.findByJobAndUser1AndUser2(job, postOwner, currentUser);
        ChatRoom chatRoom;
        if (optionalChatRoom.isPresent()) {
            chatRoom = optionalChatRoom.get();
        } else {
            chatRoom = new ChatRoom(
                    0L,
                    job,
                    postOwner,
                    currentUser,
                    UUID.randomUUID().toString(),
                    LocalDateTime.now()
            );
            chatRoomRepository.save(chatRoom);
        }

        return ResponseEntity.status(HttpStatus.OK).body(new ChatRoomIdDto(chatRoom.getRoomId()));
    }

    @Transactional(readOnly = true)
    public List<Chat> getChatIncludeChatId(String roomId) {
        Optional<ChatRoom> optionalChatRoom = chatRoomRepository.findByRoomId(roomId);
        ChatRoom chatRoom = optionalChatRoom.get();

        return chatRepository.findByChatRoomId(chatRoom.getId());
    }
}