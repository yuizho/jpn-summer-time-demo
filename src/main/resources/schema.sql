DROP TABLE IF EXISTS products;
CREATE TABLE products (
    id INT unsigned NOT NULL AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    shipped_at DATE NOT NULL
)
;

INSERT INTO products (name, shipped_at) VALUES ('Product1', '2020-01-01'), ('Product2', '1951-05-06')
;
