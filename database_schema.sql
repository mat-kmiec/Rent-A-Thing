--  *********************************************************************
--  Update Database Script
--  *********************************************************************
--  Change Log: src/main/resources/db/changelog/changelog-master.xml
--  Ran at: 29.01.2026, 02:30
--  Against: null@offline:mysql
--  Liquibase version: 4.29.2
--  *********************************************************************

--  Changeset db/changelog/releases/changes/001-create-user.xml::20260113-1::mateusz kmiec
CREATE TABLE users (id BIGINT AUTO_INCREMENT NOT NULL, first_name VARCHAR(50) NOT NULL, last_name VARCHAR(50) NOT NULL, email VARCHAR(100) NOT NULL, password VARCHAR(255) NOT NULL, `role` VARCHAR(20) NOT NULL, enabled TINYINT(1) DEFAULT 1 NOT NULL, `locked` TINYINT(1) DEFAULT 0 NOT NULL, CONSTRAINT PK_USERS PRIMARY KEY (id), UNIQUE (email));

--  Changeset db/changelog/releases/changes/003-create-categories.xml::20260114-2::mateusz kmiec
CREATE TABLE categories (id BIGINT AUTO_INCREMENT NOT NULL, name VARCHAR(255) NOT NULL, `description` VARCHAR(255) NULL, icon_class VARCHAR(100) NULL, CONSTRAINT PK_CATEGORIES PRIMARY KEY (id), UNIQUE (name));

--  Changeset db/changelog/releases/changes/002-create-items.xml::20260114-1::mateusz kmiec
CREATE TABLE items (id BIGINT AUTO_INCREMENT NOT NULL, sku VARCHAR(50) NOT NULL, title VARCHAR(100) NOT NULL, `description` VARCHAR(5000) NOT NULL, price_per_day DECIMAL(10, 2) NOT NULL, discounted_percent INT NULL, deposit TINYINT(1) DEFAULT 0 NULL, deposit_price DECIMAL(10, 2) NULL, image_url VARCHAR(1024) NULL, category_id BIGINT NOT NULL, average_rating DOUBLE NULL, review_count INT DEFAULT 0 NULL, available TINYINT(1) DEFAULT 1 NULL, can_be_shipped TINYINT(1) DEFAULT 1 NULL, can_be_picked_up TINYINT(1) DEFAULT 1 NULL, shipping_price DECIMAL(10, 2) NULL, min_rental_days INT DEFAULT 1 NULL, is_new TINYINT(1) DEFAULT 1 NULL, created_at timestamp NULL, last_modified timestamp NULL, CONSTRAINT PK_ITEMS PRIMARY KEY (id), CONSTRAINT fk_items_category FOREIGN KEY (category_id) REFERENCES categories(id), UNIQUE (sku), UNIQUE (title));

--  Changeset db/changelog/releases/changes/004-create-review.xml::20260115-1::mateusz kmiec
CREATE TABLE reviews (id BIGINT AUTO_INCREMENT NOT NULL, author_name VARCHAR(255) NOT NULL, content VARCHAR(255) NULL, rating INT NOT NULL, item_id BIGINT NOT NULL, created_at timestamp DEFAULT NOW() NOT NULL, CONSTRAINT PK_REVIEWS PRIMARY KEY (id));

ALTER TABLE reviews ADD CONSTRAINT fk_review_item FOREIGN KEY (item_id) REFERENCES items (id) ON DELETE CASCADE;

CREATE INDEX idx_review_item_id ON reviews(item_id);

--  Changeset db/changelog/releases/changes/005-create-rentals.xml::20260114-3::mateusz kmiec
CREATE TABLE rentals (id BIGINT AUTO_INCREMENT NOT NULL, item_id BIGINT NOT NULL, user_id BIGINT NOT NULL, start_date_time timestamp NOT NULL, end_date_time timestamp NOT NULL, return_date_time timestamp NULL, total_cost DECIMAL(10, 2) NULL, deposit DECIMAL(10, 2) NULL, deposit_paid TINYINT(1) DEFAULT 0 NULL, status VARCHAR(20) NOT NULL, delivery_method VARCHAR(20) NULL, payment_method VARCHAR(20) NULL, shipping_cost DECIMAL(10, 2) NULL, invoice_requested TINYINT(1) DEFAULT 0 NULL, hand_over_notes VARCHAR(1024) NULL, return_notes VARCHAR(1024) NULL, created_at timestamp NULL, CONSTRAINT PK_RENTALS PRIMARY KEY (id), CONSTRAINT fk_rentals_item FOREIGN KEY (item_id) REFERENCES items(id), CONSTRAINT fk_rentals_user FOREIGN KEY (user_id) REFERENCES users(id));

--  Changeset db/changelog/releases/changes/005-create-address.xml::005-create-address::mat-kmiec
CREATE TABLE addresses (id BIGINT AUTO_INCREMENT NOT NULL, street VARCHAR(100) NOT NULL, house_number VARCHAR(20) NOT NULL, apartment_number VARCHAR(20) NULL, city VARCHAR(100) NOT NULL, zip_code VARCHAR(10) NOT NULL, user_id BIGINT NOT NULL, CONSTRAINT PK_ADDRESSES PRIMARY KEY (id), CONSTRAINT fk_address_user FOREIGN KEY (user_id) REFERENCES users(id), UNIQUE (user_id));

--  Changeset db/changelog/releases/changes/006-update-users-table.xml::20260123-1::antigravity
ALTER TABLE users ADD phone_number VARCHAR(20) NULL, ADD notif_email TINYINT(1) DEFAULT 1 NOT NULL, ADD notif_sms TINYINT(1) DEFAULT 0 NOT NULL, ADD newsletter TINYINT(1) DEFAULT 0 NOT NULL;

--  Changeset db/changelog/releases/data/001-seed-user.xml::20260113-2-seed-users::mateusz kmiec
INSERT INTO users (first_name, last_name, email, password, `role`, enabled, `locked`) VALUES ('Jan', 'Kowalski', 'admin@admin.pl', '$2a$10$Rr49N6mdjbSPLlXttYMbCOfAFEzPg/ZcLqPn9DWszt2zAO/20HocC', 'ROLE_ADMIN', 1, 0);

INSERT INTO users (first_name, last_name, email, password, `role`, enabled, `locked`) VALUES ('Marta', 'Nowak', 'user@user.com', '$2a$10$paAaspYGsNEJgLlCURtVlOIpfrWwhg6HoOxGX7fnNuB0ftQwImE.y', 'ROLE_USER', 1, 0);

INSERT INTO users (first_name, last_name, email, password, `role`, enabled, `locked`) VALUES ('Krzysztof', 'Jakiś', 'krzysztof.jakis@example.com', '$2a$10$8g5n2bJ1qK4vL6m9oP3sRtXyZaB2cD5eF7gH8iJ0kL1mN2oP3qR4s', 'ROLE_USER', 1, 1);

INSERT INTO users (first_name, last_name, email, password, `role`, enabled, `locked`) VALUES ('Niewiem', 'Bezimienny', 'nwmbez@example.com', '$2a$10$VvYHFj6K8L9w0N7r4M3qRuFc1N5vX8m3k6L7P8q0R1s2T3u4V5w6X', 'ROLE_USER', 0, 0);

--  Changeset db/changelog/releases/data/003-seed-categories.xml::0::mateusz kmiec
INSERT INTO categories (name, `description`, icon_class) VALUES ('Elektronika', 'Aparaty, drony, laptopy i inne gadżety.', 'fa-camera');

INSERT INTO categories (name, `description`, icon_class) VALUES ('Dom i Ogród', 'Narzędzia budowlane, kosiarki, meble ogrodowe.', 'fa-hammer');

INSERT INTO categories (name, `description`, icon_class) VALUES ('Pojazdy', 'Samochody, motocykle, hulajnogi elektryczne.', 'fa-car');

INSERT INTO categories (name, `description`, icon_class) VALUES ('Sport i Turystyka', 'Namioty, rowery, sprzęt narciarski.', 'fa-bicycle');

INSERT INTO categories (name, `description`, icon_class) VALUES ('Dla Dzieci', 'Wózki, foteliki, zabawki ogrodowe.', 'fa-baby-carriage');

--  Changeset db/changelog/releases/data/002-seed-items.xml::20260114-2::mateusz kmiec
INSERT INTO items (sku, title, `description`, price_per_day, discounted_percent, deposit, deposit_price, image_url, category_id, average_rating, review_count, available, can_be_shipped, can_be_picked_up, shipping_price, min_rental_days, is_new, created_at) VALUES ('SON-A7III-001', 'Sony Alpha a7 III', 'Pełnoklatkowy aparat bezlusterkowy Sony Alpha a7 III...', '120.00', '10', 1, '500.00', 'https://images.unsplash.com/photo-1516035069371-29a1b244cc32', 1, 4.8, 12, 1, 1, 1, '19.99', 1, 0, NOW());

INSERT INTO items (sku, title, `description`, price_per_day, discounted_percent, deposit, deposit_price, image_url, category_id, average_rating, review_count, available, can_be_shipped, can_be_picked_up, shipping_price, min_rental_days, is_new, created_at) VALUES ('DJI-MINI3-002', 'Dron DJI Mini 3 Pro', 'Lekki i kompaktowy dron z kamerą 4K, idealny do podróży.', '150.00', '0', 1, '1000.00', 'https://images.unsplash.com/photo-1508614589041-895b88991e3e', 1, 4.9, 8, 1, 1, 1, '25.00', 1, 1, NOW());

INSERT INTO items (sku, title, `description`, price_per_day, discounted_percent, deposit, deposit_price, image_url, category_id, average_rating, review_count, available, can_be_shipped, can_be_picked_up, shipping_price, min_rental_days, is_new, created_at) VALUES ('MAK-HR2470-003', 'Młotowiertarka Makita HR2470', 'Profesjonalna młotowiertarka do ciężkich prac remontowych.', '45.00', '5', 1, '200.00', 'https://images.unsplash.com/photo-1504148455328-c376907d081c', 2, 4.7, 24, 1, 0, 1, '0.00', 1, 0, NOW());

INSERT INTO items (sku, title, `description`, price_per_day, discounted_percent, deposit, deposit_price, image_url, category_id, average_rating, review_count, available, can_be_shipped, can_be_picked_up, shipping_price, min_rental_days, is_new, created_at) VALUES ('XIA-M365-004', 'Hulajnoga elektryczna Xiaomi M365', 'Szybki i ekologiczny transport miejski, zasięg do 30km.', '60.00', '0', 1, '300.00', 'https://images.unsplash.com/photo-1597075095400-b247f1850d9c', 3, 4.5, 15, 1, 0, 1, '0.00', 1, 0, NOW());

INSERT INTO items (sku, title, `description`, price_per_day, discounted_percent, deposit, deposit_price, image_url, category_id, average_rating, review_count, available, can_be_shipped, can_be_picked_up, shipping_price, min_rental_days, is_new, created_at) VALUES ('THU-CHASM-005', 'Namiot 4-osobowy Thule', 'Wodoodporny, przestronny namiot rodzinny na każdą pogodę.', '80.00', '15', 1, '400.00', 'https://images.unsplash.com/photo-1504280390367-361c6d9f38f4', 4, 4.6, 5, 1, 1, 1, '29.99', 2, 1, NOW());

INSERT INTO items (sku, title, `description`, price_per_day, discounted_percent, deposit, deposit_price, image_url, category_id, average_rating, review_count, available, can_be_shipped, can_be_picked_up, shipping_price, min_rental_days, is_new, created_at) VALUES ('CYB-PRIAM-006', 'Wózek Cybex Priam 2w1', 'Luksusowy wózek głęboko-spacerowy, doskonała amortyzacja.', '110.00', '0', 1, '600.00', 'https://images.unsplash.com/photo-1591123120675-6f7f1aae0e5b', 5, 5.0, 3, 1, 0, 1, '0.00', 3, 0, NOW());

INSERT INTO items (sku, title, `description`, price_per_day, discounted_percent, deposit, deposit_price, image_url, category_id, average_rating, review_count, available, can_be_shipped, can_be_picked_up, shipping_price, min_rental_days, is_new, created_at) VALUES ('VR-QUEST3-007', 'Gogle VR Meta Quest 3 512GB', 'Najnowsze gogle VR/MR. W zestawie zainstalowane gry: Beat Saber i Superhot.', '95.00', '0', 1, '800.00', 'https://images.unsplash.com/photo-1622979135225-d2ba269cf1ac', 1, 4.9, 18, 1, 1, 1, '21.00', 2, 1, NOW());

INSERT INTO items (sku, title, `description`, price_per_day, discounted_percent, deposit, deposit_price, image_url, category_id, average_rating, review_count, available, can_be_shipped, can_be_picked_up, shipping_price, min_rental_days, is_new, created_at) VALUES ('KAR-PZZI-008', 'Odkurzacz piorący Karcher Puzzi 10/1', 'Profesjonalny sprzęt do czyszczenia tapicerek i dywanów. Chemia w zestawie.', '70.00', '10', 1, '350.00', 'https://images.unsplash.com/photo-1558317374-067fb5f30001', 2, 4.8, 42, 1, 0, 1, '0.00', 1, 0, NOW());

INSERT INTO items (sku, title, `description`, price_per_day, discounted_percent, deposit, deposit_price, image_url, category_id, average_rating, review_count, available, can_be_shipped, can_be_picked_up, shipping_price, min_rental_days, is_new, created_at) VALUES ('TRA-PLAT-009', 'Przyczepa platforma 4.5m', 'Dwuosiowa przyczepa o ładowności do 2000kg. Idealna do transportu gabarytów.', '130.00', '0', 1, '500.00', 'https://images.unsplash.com/photo-1586191582151-f73770739972', 3, 4.4, 6, 1, 0, 1, '0.00', 1, 0, NOW());

INSERT INTO items (sku, title, `description`, price_per_day, discounted_percent, deposit, deposit_price, image_url, category_id, average_rating, review_count, available, can_be_shipped, can_be_picked_up, shipping_price, min_rental_days, is_new, created_at) VALUES ('SUP-AZTRON-010', 'Deska SUP Aztron Mercury 10.10', 'Pompowana deska do pływania na stojąco. W zestawie wiosło, pompka i plecak.', '55.00', '5', 1, '400.00', 'https://images.unsplash.com/photo-1517055729445-fa7d27394b48', 4, 4.7, 11, 1, 1, 1, '35.00', 2, 1, NOW());

INSERT INTO items (sku, title, `description`, price_per_day, discounted_percent, deposit, deposit_price, image_url, category_id, average_rating, review_count, available, can_be_shipped, can_be_picked_up, shipping_price, min_rental_days, is_new, created_at) VALUES ('LEGO-MILL-011', 'Zestaw LEGO Star Wars Sokół Millennium', 'Gigantyczny zestaw (7500+ elementów) do złożenia na miejscu. Wynajem na tygodnie.', '40.00', '0', 1, '1500.00', 'https://images.unsplash.com/photo-1585366119957-e9730b6d0f60', 5, 5.0, 4, 1, 0, 1, '0.00', 7, 0, NOW());

INSERT INTO items (sku, title, `description`, price_per_day, discounted_percent, deposit, deposit_price, image_url, category_id, average_rating, review_count, available, can_be_shipped, can_be_picked_up, shipping_price, min_rental_days, is_new, created_at) VALUES ('PROJ-EPS-4K-012', 'Projektor 4K Epson EH-TW7000', 'Profesjonalny projektor do kina domowego. Idealny na wieczory filmowe lub mecze.', '110.00', '0', 1, '800.00', 'https://images.unsplash.com/photo-1535016120720-40c646be44da', 1, 4.8, 9, 1, 1, 1, '25.00', 1, 0, NOW());

INSERT INTO items (sku, title, `description`, price_per_day, discounted_percent, deposit, deposit_price, image_url, category_id, average_rating, review_count, available, can_be_shipped, can_be_picked_up, shipping_price, min_rental_days, is_new, created_at) VALUES ('OSUSZ-BUD-013', 'Osuszacz powietrza Master DH 732', 'Wydajny osuszacz budowlany, idealny po tynkowaniu lub zalaniu mieszkania.', '40.00', '20', 1, '400.00', 'https://images.unsplash.com/photo-1581092160562-40aa08e78837', 2, 4.9, 14, 1, 0, 1, '0.00', 3, 1, NOW());

INSERT INTO items (sku, title, `description`, price_per_day, discounted_percent, deposit, deposit_price, image_url, category_id, average_rating, review_count, available, can_be_shipped, can_be_picked_up, shipping_price, min_rental_days, is_new, created_at) VALUES ('BAG-ROW-THU-014', 'Bagażnik rowerowy Thule na hak (4 rowery)', 'Bezpieczny transport 4 rowerów. Odchylany, co umożliwia dostęp do bagażnika.', '35.00', '0', 1, '600.00', 'https://images.unsplash.com/photo-1591741535018-d042766c62eb', 3, 4.7, 21, 1, 0, 1, '0.00', 3, 0, NOW());

INSERT INTO items (sku, title, `description`, price_per_day, discounted_percent, deposit, deposit_price, image_url, category_id, average_rating, review_count, available, can_be_shipped, can_be_picked_up, shipping_price, min_rental_days, is_new, created_at) VALUES ('GARMIN-FENIX7-015', 'Zegarek sportowy Garmin Fenix 7X Sapphire', 'Zegarek outdoorowy z mapami GPS. Przetestuj przed wyprawą w góry.', '50.00', '0', 1, '1200.00', 'https://images.unsplash.com/photo-1523275335684-37898b6baf30', 4, 5.0, 5, 1, 1, 1, '18.00', 2, 1, NOW());

INSERT INTO items (sku, title, `description`, price_per_day, discounted_percent, deposit, deposit_price, image_url, category_id, average_rating, review_count, available, can_be_shipped, can_be_picked_up, shipping_price, min_rental_days, is_new, created_at) VALUES ('ZAMEK-DMUCH-016', 'Dmuchany zamek ze zjeżdżalnią (Urodziny)', 'Atrakcja na przyjęcia dla dzieci. Wymiary 4x3m. W zestawie dmuchawa.', '250.00', '0', 1, '500.00', 'https://images.unsplash.com/photo-1572953108210-0dd2b1da8271', 5, 4.6, 7, 1, 0, 1, '0.00', 1, 0, NOW());

--  Changeset db/changelog/releases/data/004-seed-review.xml::20260115-3-bulk-seed-reviews::Mateusz Kmiec
INSERT INTO reviews (author_name, content, rating, item_id, created_at) VALUES ('Adam Nowak', 'Bardzo polecam, sprzęt wysokiej klasy.', 5, 1, NOW());

INSERT INTO reviews (author_name, content, rating, item_id, created_at) VALUES ('Katarzyna W.', 'Wszystko super, szybki kontakt.', 5, 1, NOW());

INSERT INTO reviews (author_name, content, rating, item_id, created_at) VALUES ('Piotr Z.', 'Sprzęt działa bez zarzutu, polecam.', 4, 1, NOW());

INSERT INTO reviews (author_name, content, rating, item_id, created_at) VALUES ('Anna Maria', 'Trochę porysowana obudowa, ale technicznie ok.', 3, 1, NOW());

INSERT INTO reviews (author_name, content, rating, item_id, created_at) VALUES ('Michał K.', 'Najlepszy wynajem w okolicy.', 5, 1, NOW());

INSERT INTO reviews (author_name, content, rating, item_id, created_at) VALUES ('Zofia S.', 'Bardzo czysty i zadbany sprzęt.', 5, 1, NOW());

INSERT INTO reviews (author_name, content, rating, item_id, created_at) VALUES ('Tomasz B.', 'Bateria trzyma krócej niż myślałem.', 3, 1, NOW());

INSERT INTO reviews (author_name, content, rating, item_id, created_at) VALUES ('Magda G.', 'Rewelacja! Na pewno skorzystam ponownie.', 5, 1, NOW());

INSERT INTO reviews (author_name, content, rating, item_id, created_at) VALUES ('Robert J.', 'Dobry stosunek ceny do jakości.', 4, 1, NOW());

INSERT INTO reviews (author_name, content, rating, item_id, created_at) VALUES ('Julia O.', 'Wszystko zgodnie z opisem.', 5, 1, NOW());

INSERT INTO reviews (author_name, content, rating, item_id, created_at) VALUES ('Krzysztof L.', 'Sprawnie i profesjonalnie.', 4, 1, NOW());

INSERT INTO reviews (author_name, content, rating, item_id, created_at) VALUES ('Beata M.', 'Nie mam zastrzeżeń.', 5, 1, NOW());

INSERT INTO reviews (author_name, content, rating, item_id, created_at) VALUES ('Paweł W.', 'Super kontakt z właścicielem.', 5, 1, NOW());

INSERT INTO reviews (author_name, content, rating, item_id, created_at) VALUES ('Monika K.', 'Aparat idealny na wesele.', 5, 1, NOW());

INSERT INTO reviews (author_name, content, rating, item_id, created_at) VALUES ('Łukasz P.', 'Solidnie zapakowane na czas transportu.', 4, 1, NOW());

INSERT INTO reviews (author_name, content, rating, item_id, created_at) VALUES ('Dominika F.', 'Polecam każdemu.', 5, 1, NOW());

INSERT INTO reviews (author_name, content, rating, item_id, created_at) VALUES ('Artur D.', 'Daję 4 gwiazdki bo brakowało instrukcji.', 4, 1, NOW());

INSERT INTO reviews (author_name, content, rating, item_id, created_at) VALUES ('Szymon R.', 'Działa doskonale.', 5, 1, NOW());

INSERT INTO reviews (author_name, content, rating, item_id, created_at) VALUES ('Alicja B.', 'Bardzo elastyczne godziny zwrotu.', 5, 1, NOW());

INSERT INTO reviews (author_name, content, rating, item_id, created_at) VALUES ('Grzegorz H.', 'Solidny sprzęt do pracy.', 5, 1, NOW());

UPDATE items SET average_rating = 4.7, review_count = 22 WHERE id = 1;

--  Changeset db/changelog/releases/data/005-seed-rentals.xml::20260119-data-testowa-rentals-v2::mateusz kmiec
INSERT INTO rentals (item_id, user_id, start_date_time, end_date_time, return_date_time, status, total_cost, created_at) VALUES (1, 1, '2026-02-01 10:00:00', '2026-02-03 10:00:00', '2026-02-03 09:45:00', 'COMPLETED', 150.00, '2026-01-10 12:00:00');

INSERT INTO rentals (item_id, user_id, start_date_time, end_date_time, status, deposit, deposit_paid, created_at) VALUES (1, 1, '2026-02-10 09:00:00', '2026-02-15 18:00:00', 'ACTIVE', 50.00, 1, '2026-02-05 10:00:00');

INSERT INTO rentals (item_id, user_id, start_date_time, end_date_time, status, payment_method, created_at) VALUES (1, 1, '2026-02-20 08:00:00', '2026-02-22 20:00:00', 'PENDING', 'CARD', '2026-02-15 15:00:00');

INSERT INTO rentals (item_id, user_id, start_date_time, end_date_time, status, created_at) VALUES (1, 1, '2026-02-25 08:00:00', '2026-02-26 10:00:00', 'OVERDUE', '2026-02-15 09:00:00');

