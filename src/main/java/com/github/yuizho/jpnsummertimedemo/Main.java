package com.github.yuizho.jpnsummertimedemo;

import org.slf4j.Logger;
import org.testcontainers.containers.MySQLContainer;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Main {
    private static final String MYSQL_DB = "db";
    private static final String MYSQL_USER = "root";
    private static final String MYSQL_PASSWORD = "password";
    private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(Main.class);


    public static void main(String[] args) {
        try (var mysqlContainer = new MySQLContainer<>("mysql:8.1")
                .withDatabaseName(MYSQL_DB)
                .withUsername(MYSQL_USER)
                .withPassword(MYSQL_PASSWORD)
                .withInitScript("schema.sql")
        ) {
            mysqlContainer.start();
            LOGGER.info("MySQLコンテナが起動しました {}", mysqlContainer.getJdbcUrl());

            try (var conn = DriverManager.getConnection(
                    "jdbc:mysql://127.0.0.1:" + mysqlContainer.getMappedPort(3306) + "/" + MYSQL_DB + "?serverTimezone=Asia/Tokyo",
                    MYSQL_USER,
                    MYSQL_PASSWORD
            );) {
                var productRepository = new ProductRepository(conn);
                var products = productRepository.findAll();
                LOGGER.info("=================productの取得に成功=================");
                products.forEach(product -> LOGGER.info(product.toString()));
                LOGGER.info("===================================================");
            } catch (SQLException e) {
                throw new RuntimeException("MySQLへの接続に失敗", e);
            }

            LOGGER.info("MySQLコンテナを停止して終了します");
        }
    }
}

class ProductRepository {
    private final Connection conn;
    ProductRepository(Connection conn) {
        this.conn = conn;
    }

    List<Product> findAll() {
        try (var stmt = conn.createStatement();) {
            try (var rs = stmt.executeQuery("SELECT id, name, shipped_at FROM products")) {
                var products = new ArrayList<Product>();
                while (rs.next()) {
                    var id = rs.getInt("id");
                    var name = rs.getString("name");
                    var shippedAt = rs.getTimestamp("shipped_at");
                    products.add(new Product(id, name, shippedAt));
                }
                return products;
            }
        } catch (SQLException e) {
            throw new RuntimeException("selectの実行に失敗", e);
        }
    }
}

record Product(int id, String name, Timestamp shippedAt) {
}
