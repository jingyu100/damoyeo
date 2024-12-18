package Team6.Damoyeo.Post.Service;

import Team6.Damoyeo.Post.Entity.Post;
import Team6.Damoyeo.Post.Entity.PostRequest;
import Team6.Damoyeo.Post.Repository.PostRepository;
import Team6.Damoyeo.Post.Repository.PostRequestRepository;
import Team6.Damoyeo.User.Entity.User;
import Team6.Damoyeo.User.Repository.UserRepository;
import Team6.Damoyeo.calendar.Entity.CalendarEvent;
import Team6.Damoyeo.calendar.repository.CalendarRepository;
import Team6.Damoyeo.chat.Entity.ChatParticipant;
import Team6.Damoyeo.chat.Entity.ChatRoom;
import Team6.Damoyeo.chat.repository.ChatParticipantRepository;
import Team6.Damoyeo.chat.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostRequestService {

    private final PostRequestRepository postRequestRepository;

    private final PostRepository postRepository;

    private final UserRepository userRepository;

    private final ChatRoomRepository chatRoomRepository;

    private final ChatParticipantRepository chatParticipantRepository;

    private final CalendarRepository calendarRepository;

    public void saveRequest(Integer userId, Integer postId, String text) {
        PostRequest postRequest = new PostRequest();
        Optional<User> ou = userRepository.findById(userId);
        Optional<Post> po = postRepository.findById(postId);
        User user = null;
        Post post = null;
        if (ou.isPresent()) {
            user = ou.get();
        }
        if (po.isPresent()) {
            post = po.get();
        }
        postRequest.setUser(user);
        postRequest.setPost(post);
        postRequest.setStatus("0");
        postRequest.setMessage(text);
        postRequest.setCreatedDate(LocalDateTime.now());
        postRequestRepository.save(postRequest);
    }

    public PostRequest findClick(Post post, User user) {

        Optional<PostRequest> pru = postRequestRepository.findByPostAndUser(post, user);
        if (pru.isEmpty()) {
            return null;
        }
        return pru.get();
    }

    public void delete(PostRequest postRequest) {
        postRequestRepository.delete(postRequest);
    }

    //수락했을때
//    public void accet(Integer prId) {
//        Optional<PostRequest> opr = postRequestRepository.findById(prId);
//        if (opr.isPresent()) {
//            PostRequest pr = opr.get();
//            pr.setStatus("1");
//            pr.getPost().setNowParticipants(pr.getPost().getNowParticipants() + 1);
//            postRequestRepository.save(pr);
//            postRepository.save(pr.getPost());
//        }
//    }


    // 3. 해당 게시물의 채팅방 조회 또는 생성
//        ChatRoom chatRoom = chatRoomRepository.findByPost(postRequest.getPost())
//                .orElseGet(() -> {
//                    ChatRoom newChatRoom = ChatRoom.builder()
//                            .roomName(postRequest.getPost().getTitle() + "의 채팅방")
//                            .post(postRequest.getPost())
//                            .build();
//                    return chatRoomRepository.save(newChatRoom);
//                });


    public void accept(Integer prId) {
        // 1. PostRequest 조회
        PostRequest postRequest = postRequestRepository.findById(prId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Post Request ID"));
        Post post = postRequest.getPost();

        // 현재 참가 인원 1 증가
        post.setNowParticipants(post.getNowParticipants() + 1);

        // 최대 참가 인원과 같아지면 게시글 상태를 2로 변경
        if (post.getNowParticipants() == post.getMaxParticipants()) {
            post.setStatus("2");

            // 상태가 0인 모든 요청 삭제
            List<PostRequest> postStatusZero = postRequestRepository.findByPostAndStatus(post, "0");
            postRequestRepository.deleteAll(postStatusZero);

        }

        // 현재 수락된 요청의 상태를 1 로 변경
        postRequest.setStatus("1");


        // 3. 해당 게시물의 채팅방 조회
        ChatRoom chatRoom = chatRoomRepository.findByPost(postRequest.getPost())
                .orElseThrow(() -> new IllegalArgumentException("Chat room not found for this post"));

        // 4. 채팅방 참여자로 바로 추가
        ChatParticipant participant = ChatParticipant.builder()
                .chatRoom(chatRoom)
                .user(postRequest.getUser())
                .build();
        chatParticipantRepository.save(participant);

        // 5. 캘린더 이벤트 저장
        CalendarEvent calendarEvent = CalendarEvent.builder()
                .title(post.getTitle()) // 게시물 제목
                .description(post.getTitle()) // 게시물 설명
                .startTime(post.getEndDate())
                .endTime(post.getEndDate()) // 게시물의 종료 날짜 사용
                .createdDate(LocalDateTime.now()) // 이벤트 생성 날짜
                .user(postRequest.getUser()) // 요청한 사용자
                .post(post) // 해당 포스트
                .build();
        calendarRepository.save(calendarEvent);

        postRequestRepository.save(postRequest);
        postRepository.save(post);
    }


    public void refusal(Integer prId) {
        Optional<PostRequest> opr = postRequestRepository.findById(prId);
        if (opr.isPresent()) {
            PostRequest pr = opr.get();
            pr.setStatus("2");
            postRequestRepository.save(pr);
        }
    }

    //거절 메서드인데 일단은 삭제로 함
    public void rejected(Integer prId) {
        Optional<PostRequest> opr = postRequestRepository.findById(prId);
        if (opr.isPresent()) {
            PostRequest pr = opr.get();
//            pr.setStatus("3");
//            postRequestRepository.save(pr);
            postRequestRepository.delete(pr);
        }
    }

    //거절 메서드인데 일단은 삭제로 함
    public void kicked(Integer prId) {
        Optional<PostRequest> opr = postRequestRepository.findById(prId);
        if (opr.isPresent()) {
            PostRequest pr = opr.get();
            pr.setStatus("6");
            postRequestRepository.save(pr);
        }
    }
}
