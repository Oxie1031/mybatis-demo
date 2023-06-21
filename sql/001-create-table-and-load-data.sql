DROP TABLE IF EXISTS movies;

CREATE TABLE movies (
  id VARCHAR(36),
  name VARCHAR(100) NOT NULL,
  director VARCHAR(100) NOT NULL,
  year INT NOT NULL,
  rating DECIMAL(3,1),
  runtime INT,
  PRIMARY KEY(id)
);


--ALTER TABLE movies
--ADD COLUMN rating DECIMAL(3,1),
--ADD COLUMN runtime INT;



-- 1980年代の映画
INSERT INTO movies (id, name, director, year, rating, runtime) VALUES (UUID(), "ハリー・ポッターと賢者の石", "クリス・コロンバス", 1980, 7.6, 152);
INSERT INTO movies (id, name, director, year, rating, runtime) VALUES (UUID(), "ブレードランナー", "リドリー・スコット", 1982, 8.1, 117);
INSERT INTO movies (id, name, director, year, rating, runtime) VALUES (UUID(), "バック・トゥ・ザ・フューチャー", "ロバート・ゼメキス", 1985, 8.5, 116);
INSERT INTO movies (id, name, director, year, rating, runtime) VALUES (UUID(), "ダイ・ハード", "ジョン・マクティアナン", 1988, 8.2, 132);
INSERT INTO movies (id, name, director, year, rating, runtime) VALUES (UUID(), "インディ・ジョーンズ/最後の聖戦", "スティーブン・スピルバーグ", 1989, 8.3, 127);

-- 1990年代の映画
INSERT INTO movies (id, name, director, year, rating, runtime) VALUES (UUID(), "ショーシャンクの空に", "フランク・ダラボン", 1994, 9.3, 142);
INSERT INTO movies (id, name, director, year, rating, runtime) VALUES (UUID(), "パルプ・フィクション", "クエンティン・タランティーノ", 1994, 8.9, 154);
INSERT INTO movies (id, name, director, year, rating, runtime) VALUES (UUID(), "フォレスト・ガンプ", "ロバート・ゼメキス", 1994, 8.8, 142);
INSERT INTO movies (id, name, director, year, rating, runtime) VALUES (UUID(), "マトリックス", "ウォシャウスキー姉弟", 1999, 8.7, 136);
INSERT INTO movies (id, name, director, year, rating, runtime) VALUES (UUID(), "ファイト・クラブ", "デビッド・フィンチャー", 1999, 8.8, 139);

-- 2000年代の映画
INSERT INTO movies (id, name, director, year, rating, runtime) VALUES (UUID(), "グラディエーター", "リドリー・スコット", 2000, 8.5, 155);
INSERT INTO movies (id, name, director, year, rating, runtime) VALUES (UUID(), "ロード・オブ・ザ・リング", "ピーター・ジャクソン", 2001, 8.8, 178);
INSERT INTO movies (id, name, director, year, rating, runtime) VALUES (UUID(), "ハリー・ポッターと秘密の部屋", "クリス・コロンバス", 2002, 7.4, 161);
INSERT INTO movies (id, name, director, year, rating, runtime) VALUES (UUID(), "ダークナイト", "クリストファー・ノーラン", 2008, 9.0, 152);
INSERT INTO movies (id, name, director, year, rating, runtime) VALUES (UUID(), "アバター", "ジェームズ・キャメロン", 2009, 7.8, 162);

-- 2010年代の映画
INSERT INTO movies (id, name, director, year, rating, runtime) VALUES (UUID(), "インセプション", "クリストファー・ノーラン", 2010, 8.8, 148);
INSERT INTO movies (id, name, director, year, rating, runtime) VALUES (UUID(), "アベンジャーズ", "ジョス・ウィードン", 2012, 8.0, 143);
INSERT INTO movies (id, name, director, year, rating, runtime) VALUES (UUID(), "ラ・ラ・ランド", "ダミアン・チャゼル", 2016, 8.0, 128);
INSERT INTO movies (id, name, director, year, rating, runtime) VALUES (UUID(), "スターウォーズ: 最後のジェダイ", "ライアン・ジョンソン", 2017, 7.0, 152);
INSERT INTO movies (id, name, director, year, rating, runtime) VALUES (UUID(), "ジョーカー", "トッド・フィリップス", 2019, 8.5, 122);

-- 2020年代の映画
INSERT INTO movies (id, name, director, year, rating, runtime) VALUES (UUID(), "テネット", "クリストファー・ノーラン", 2020, 7.4, 150);
INSERT INTO movies (id, name, director, year, rating, runtime) VALUES (UUID(), "ソウルフル・ワールド", "ピート・ドクター", 2020, 8.1, 100);
INSERT INTO movies (id, name, director, year, rating, runtime) VALUES (UUID(), "DUNE デューン 砂の惑星", "ドニー・ヴィルヌーヴ", 2021, 8.3, 155);
INSERT INTO movies (id, name, director, year, rating, runtime) VALUES (UUID(), "ノー・タイム・トゥ・ダイ", "キャリー・フクナガ", 2021, 7.2, 163);
INSERT INTO movies (id, name, director, year, rating, runtime) VALUES (UUID(), "マトリックス レザレクションズ", "ラナ・ウォシャウスキー", 2021, 5.7, 148);
