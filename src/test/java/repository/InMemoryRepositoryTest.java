package repository;

import model.Student;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.PagedList;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryRepositoryTest {

    static class TestStudentRepository extends InMemoryRepository<Student, String> {
        @Override
        protected String getId(Student entity) {
            return entity.getId();
        }
    }

    private TestStudentRepository repo;

    @BeforeEach
    void setUp() {
        repo = new TestStudentRepository();
    }

    @Test
    void testSaveAndFindById() {
        Student s1 = new Student("S1", "Alice", "alice@mail.com", "CS", s -> 0.0);
        Student s2 = new Student("S2", "Bob", "bob@mail.com", "EE", s -> 0.0);

        repo.save(s1);
        repo.save(s2);

        Optional<Student> foundS1 = repo.findById("S1");
        assertTrue(foundS1.isPresent());
        assertEquals("Alice", foundS1.get().getName());

        Optional<Student> foundS2 = repo.findById("S2");
        assertTrue(foundS2.isPresent());
        assertEquals("Bob", foundS2.get().getName());

        // Non-existing ID
        Optional<Student> notFound = repo.findById("S3");
        assertTrue(notFound.isEmpty());
    }

    @Test
    void testFindAll() {
        Student s1 = new Student("S1", "Alice", "alice@mail.com", "CS", s -> 0.0);
        Student s2 = new Student("S2", "Bob", "bob@mail.com", "EE", s -> 0.0);
        repo.save(s1);
        repo.save(s2);

        List<Student> allStudents = repo.findAll();
        assertEquals(2, allStudents.size());
        assertTrue(allStudents.contains(s1));
        assertTrue(allStudents.contains(s2));
    }

    @Test
    void testPagedList() {
        for (int i = 1; i <= 10; i++) {
            repo.save(new Student("S" + i, "Student" + i, "s" + i + "@mail.com", "CS", s -> 0.0));
        }

        PagedList<Student> page1 = repo.findAll(0, 3);
        assertEquals(3, page1.getPageItems().size());
        assertEquals(10, page1.getTotalCount());

        PagedList<Student> page4 = repo.findAll(3, 3);
        assertEquals(1, page4.getPageItems().size()); // last page only 1
    }

    @Test
    void testDeleteById() {
        Student s1 = new Student("S1", "Alice", "alice@mail.com", "CS", s -> 0.0);
        repo.save(s1);

        assertTrue(repo.findById("S1").isPresent());

        repo.deleteById("S1");
        assertTrue(repo.findById("S1").isEmpty());

        // Deleting non-existent ID should not throw
        repo.deleteById("S2");
    }
}
