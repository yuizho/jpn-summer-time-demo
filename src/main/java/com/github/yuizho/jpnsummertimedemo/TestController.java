package com.github.yuizho.jpnsummertimedemo;

import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/test")
public class TestController {
    private final NamedParameterJdbcOperations jdbcTemplate;

    public TestController(NamedParameterJdbcOperations jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping("")
    public String test() {
        return jdbcTemplate.query(
                        "SELECT * FROM products",
                        (rs, rowNum) ->
                                String.format(
                                        "(%d, %s, %s)",
                                        rs.getInt("id"),
                                        rs.getString("name"),
                                        rs.getTimestamp("shipped_at")
                                )

                ).stream()
                .collect(Collectors.joining(", "));
    }
}
