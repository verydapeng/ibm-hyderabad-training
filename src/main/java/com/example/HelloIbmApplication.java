package com.example;

import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.Size;

@SpringBootApplication
public class HelloIbmApplication {

    public static void main(String[] args) {
        SpringApplication.run(HelloIbmApplication.class, args);
    }

    @Bean
    ApplicationRunner init(PersonRepo repo) {
        return args -> {
            repo.save(new Person("Dapeng"));
        };
    }

}


@Configuration
class MyAppConfig {

    @Bean
    PersonService createPersonService() {
        if ("1".equals(System.getProperty("useRandom"))) {
            return new RandomPersonService();
        } else {
            return new StubPersonService();
        }
    }
}


interface PersonRepo extends JpaRepository<Person, Long> {
    // select * from person where name = ?
    Person findByName(String name);

    @Transactional
    @Modifying
    @Query(nativeQuery = true, value = "delete from person")
    void nukeTheTable();

}

@Entity
class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    long id;

    @Size(min = 6)
    String name;

    String phone;

    public Person() { }

    public Person(String name) {
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}


@RestController
class PersonController {

    private final PersonRepo repo;

    PersonController(PersonRepo repo) {this.repo = repo;}

    @PostMapping("/register")
    String register(@Valid @RequestBody Person person) {
        System.out.println("dffsdfadfasf");
        repo.save(person);
        return person.getName();
    }

    @GetMapping("/test")
    Object test() {
        return "adfsad" + new Person("dapeng");
    }

    @GetMapping("/listAll")
    Object list() {
        return repo.findAll();
    }

    @GetMapping("/find")
    Object find(@RequestParam String name) {
        return repo.findByName(name);
    }

    @GetMapping("/nuke")
    String nuke() {
        repo.nukeTheTable();

        return "done";
    }
}

interface PersonService {

    String getName(int id);
}

class RandomPersonService implements PersonService {

    @Override
    public String getName(int id) {
        return "name for " + Math.random();
    }
}

class StubPersonService implements PersonService {

    @Override
    public String getName(int id) {
        return "name for " + id;
    }
}