CREATE TABLE IF NOT EXISTS users (
                                     id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                     first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT FALSE,
    balance DECIMAL(12, 2) NOT NULL DEFAULT 0,
    activation_code VARCHAR(255),
    reset_password_code VARCHAR(255)
    );

CREATE TABLE IF NOT EXISTS lots (
                                    id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                    title VARCHAR(200) NOT NULL,
    description TEXT NOT NULL,
    start_price DECIMAL(12, 2) NOT NULL,
    current_price DECIMAL(12, 2) NOT NULL,
    min_bid DECIMAL(12, 2) NOT NULL,
    start_time DATETIME NOT NULL,
    end_time DATETIME NOT NULL,
    status VARCHAR(30) NOT NULL,
    owner_id BIGINT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_lots_owner
    FOREIGN KEY (owner_id) REFERENCES users(id)
    );

CREATE TABLE IF NOT EXISTS lot_images (
                                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                          lot_id BIGINT NOT NULL,
                                          file_name VARCHAR(255) NOT NULL,

    CONSTRAINT fk_lot_images_lot
    FOREIGN KEY (lot_id) REFERENCES lots(id)
    ON DELETE CASCADE
    );

CREATE TABLE IF NOT EXISTS comments (
                                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                        lot_id BIGINT NOT NULL,
                                        user_id BIGINT NOT NULL,
                                        text TEXT NOT NULL,
                                        created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

                                        CONSTRAINT fk_comments_lot
                                        FOREIGN KEY (lot_id) REFERENCES lots(id)
    ON DELETE CASCADE,

    CONSTRAINT fk_comments_user
    FOREIGN KEY (user_id) REFERENCES users(id)
    );

CREATE TABLE IF NOT EXISTS bids (
                                    id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                    lot_id BIGINT NOT NULL,
                                    user_id BIGINT NOT NULL,
                                    amount DECIMAL(12, 2) NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_bids_lot
    FOREIGN KEY (lot_id) REFERENCES lots(id)
    ON DELETE CASCADE,

    CONSTRAINT fk_bids_user
    FOREIGN KEY (user_id) REFERENCES users(id)
    );

CREATE TABLE IF NOT EXISTS subscriptions (
                                             id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                             lot_id BIGINT NOT NULL,
                                             user_id BIGINT NOT NULL,
                                             created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

                                             CONSTRAINT fk_subscriptions_lot
                                             FOREIGN KEY (lot_id) REFERENCES lots(id)
    ON DELETE CASCADE,

    CONSTRAINT fk_subscriptions_user
    FOREIGN KEY (user_id) REFERENCES users(id),

    CONSTRAINT uq_subscription UNIQUE (lot_id, user_id)
    );