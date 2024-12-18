package Team6.Damoyeo.calendar.repository;

import Team6.Damoyeo.Post.Entity.Post;
import Team6.Damoyeo.User.Entity.User;
import Team6.Damoyeo.calendar.Entity.CalendarEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

public interface CalendarRepository extends JpaRepository<CalendarEvent, Integer> {
    List<CalendarEvent> findByUser(User user);

    List<CalendarEvent> findByPost(Post post);

    Optional<CalendarEvent> findByPostAndUser(Post post, User user);
//    List<CalendarEvent> findByPostAndUser(Post post, User user);
}