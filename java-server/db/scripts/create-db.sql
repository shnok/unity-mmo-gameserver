-- ZONELIST
DROP TABLE IF EXISTS ZONELIST;
CREATE TABLE ZONELIST (
	ID VARCHAR(5) NOT NULL,
	ORIG_X DECIMAL(65535,32767) DEFAULT 0 NOT NULL,
	ORIG_Y DECIMAL(65535,32767) DEFAULT 0 NOT NULL,
	ORIG_Z DECIMAL(65535,32767) DEFAULT 0 NOT NULL,
	"SIZE" DECIMAL(65535,32767) DEFAULT 624.153 NOT NULL
);
CREATE INDEX ZONELIST_ID_IDX ON ZONELIST (ID);

-- ARMORS
DROP TABLE IF EXISTS `ARMOR`;
CREATE TABLE `ARMOR` (
  `item_id` int(11) NOT NULL default '0',
  `name` varchar(70) default NULL,
  `bodypart` varchar(15) NOT NULL default '',
  `crystallizable` varchar(5) NOT NULL default '',
  `armor_type` varchar(5) NOT NULL default '',
  `weight` int(5) NOT NULL default '0',
  `material` varchar(15) NOT NULL default '',
  `crystal_type` varchar(4) NOT NULL default '',
  `avoid_modify` int(1) NOT NULL default '0',
  `duration` int(3) NOT NULL default '0',
  `p_def` int(3) NOT NULL default '0',
  `m_def` int(2) NOT NULL default '0',
  `mp_bonus` int(3) NOT NULL default '0',
  `price` int(11) NOT NULL default '0',
  `crystal_count` int(4) default NULL,
  `sellable` varchar(5) default NULL,
  `dropable` varchar(5) default NULL,
  `destroyable` varchar(5) default NULL,
  `tradeable` varchar(5) default NULL,
  PRIMARY KEY  (`item_id`)
);

 -- CHAR TEMPLATE
DROP TABLE IF EXISTS `CHAR_TEMPLATE`;
CREATE TABLE `CHAR_TEMPLATE` (
  `ClassId` int(11) NOT NULL default '0',
  `ClassName` varchar(20) NOT NULL default '',
  `RaceId` int(1) NOT NULL default '0',
  `STR` int(2) NOT NULL default '0',
  `CON` int(2) NOT NULL default '0',
  `DEX` int(2) NOT NULL default '0',
  `_INT` int(2) NOT NULL default '0',
  `WIT` int(2) NOT NULL default '0',
  `MEN` int(2) NOT NULL default '0',
  `P_ATK` int(3) NOT NULL default '0',
  `P_DEF` int(3) NOT NULL default '0',
  `M_ATK` int(3) NOT NULL default '0',
  `M_DEF` int(2) NOT NULL default '0',
  `P_SPD` int(3) NOT NULL default '0',
  `M_SPD` int(3) NOT NULL default '0',
  `ACC` int(3) NOT NULL default '0',
  `CRITICAL` int(3) NOT NULL default '0',
  `EVASION` int(3) NOT NULL default '0',
  `MOVE_SPD` int(3) NOT NULL default '0',
  `x` decimal(10,2) NOT NULL default '0',
  `y` decimal(10,2) NOT NULL default '0',
  `z` decimal(10,2) NOT NULL default '0',
  `items1` int(4) NOT NULL default '0',
  `items2` int(4) NOT NULL default '0',
  `items3` int(4) NOT NULL default '0',
  `items4` int(4) NOT NULL default '0',
  `items5` int(10) NOT NULL default '0',
  `F_COL_H` decimal(10,2) NOT NULL default '0',
  `F_COL_R` decimal(10,2) NOT NULL default '0',
  `M_COL_H` decimal(10,2) NOT NULL default '0',
  `M_COL_R` decimal(10,2) NOT NULL default '0',
  PRIMARY KEY  (`ClassId`)
);

-- CHARACTER
DROP TABLE IF EXISTS PLAYER_ITEM;
DROP TABLE IF EXISTS `character`;
CREATE TABLE `character`(
    id INT AUTO_INCREMENT PRIMARY KEY,
    account_name VARCHAR(64) NOT NULL,
    char_name VARCHAR(64) NOT NULL,
    title VARCHAR(64) DEFAULT '',
    race TINYINT DEFAULT 0,
    class_id TINYINT DEFAULT 0,
    access_level INT DEFAULT 0,
    online BOOLEAN DEFAULT FALSE,
    char_slot TINYINT DEFAULT 0,
    level INT DEFAULT 1,
    hp INT DEFAULT 1,
    max_hp INT DEFAULT 1,
    cp INT DEFAULT 1,
    max_cp INT DEFAULT 1,
    mp INT DEFAULT 1,
    max_mp INT DEFAULT 1,
    acc INT DEFAULT 1,
    critical INT DEFAULT 1,
    evasion INT DEFAULT 1,
    m_atk INT DEFAULT 1,
    m_def INT DEFAULT 1,
    m_spd INT DEFAULT 1,
    p_atk INT DEFAULT 1,
    p_def INT DEFAULT 1,
    p_spd INT DEFAULT 1,
    run_spd INT DEFAULT 1,
    walk_spd INT DEFAULT 1,
    str TINYINT DEFAULT 1,
    con TINYINT DEFAULT 1,
    dex TINYINT DEFAULT 1,
    _int TINYINT DEFAULT 1,
    men TINYINT DEFAULT 1,
    wit TINYINT DEFAULT 1,
    face TINYINT DEFAULT 0,
    hair_style TINYINT DEFAULT 0,
    hair_color TINYINT DEFAULT 0,
    sex TINYINT DEFAULT 0,
    heading FLOAT DEFAULT 0,
    x FLOAT DEFAULT 0,
    y FLOAT DEFAULT 0,
    z FLOAT DEFAULT 0,
    colR FLOAT DEFAULT 0,
    colH FLOAT DEFAULT 0,
    exp BIGINT DEFAULT 0,
    sp BIGINT DEFAULT 0,
    karma INT DEFAULT 0,
    pvp_kills INT DEFAULT 0,
    pk_kills INT DEFAULT 0,
    clan_id INT,
    max_weight INT DEFAULT 0,
    online_time BIGINT DEFAULT 0, 
    last_login BIGINT DEFAULT 0,
    delete_time BIGINT
);

-- PLAYER ITEM
DROP TABLE IF EXISTS PLAYER_ITEM;
CREATE TABLE PLAYER_ITEM (
	OBJECT_ID INT AUTO_INCREMENT PRIMARY KEY,
	OWNER_ID INTEGER NOT NULL,
	ITEM_ID INTEGER NOT NULL,
	COUNT INTEGER DEFAULT 1,
	ENCHANT_LEVEL TINYINT DEFAULT 0,
	LOC TINYINT NOT NULL,
	SLOT INTEGER DEFAULT 0,
	PRICE_SELL INTEGER DEFAULT 0,
	PRICE_BUY INTEGER DEFAULT 0,
	CONSTRAINT CONSTRAINT_2 PRIMARY KEY (OBJECT_ID)
);
CREATE INDEX FK_OWNER_ID_INDEX_2 ON PLAYER_ITEM (OWNER_ID);
CREATE UNIQUE INDEX PRIMARY_KEY_2 ON PLAYER_ITEM (OBJECT_ID);
ALTER TABLE PLAYER_ITEM ADD CONSTRAINT FK_OWNER_ID FOREIGN KEY (OWNER_ID) REFERENCES "CHARACTER"(ID) ON DELETE CASCADE ON UPDATE RESTRICT;

-- NPC 
DROP TABLE IF EXISTS `npc`;
CREATE TABLE `npc`(
  `id` decimal(11,0) NOT NULL default '0',
  `idTemplate` int(11) NOT NULL default '0',
  `name` varchar(200) default NULL,
  `serverSideName` int(1) default '0',
  `title` varchar(45) default '',
  `serverSideTitle` int(1) default '0',
  `class` varchar(200) default NULL,
  `collision_radius` decimal(5,2) default NULL,
  `collision_height` decimal(5,2) default NULL,
  `level` decimal(2,0) default NULL,
  `sex` varchar(6) default NULL,
  `type` varchar(20) default NULL,
  `attackrange` int(11) default NULL,
  `hp` decimal(8,0) default NULL,
  `mp` decimal(5,0) default NULL,
  `hpreg` decimal(8,2) default NULL,
  `mpreg` decimal(5,2) default NULL,
  `str` decimal(7,0) default NULL,
  `con` decimal(7,0) default NULL,
  `dex` decimal(7,0) default NULL,
  `int` decimal(7,0) default NULL,
  `wit` decimal(7,0) default NULL,
  `men` decimal(7,0) default NULL,
  `exp` decimal(9,0) default NULL,
  `sp` decimal(8,0) default NULL,
  `patk` decimal(5,0) default NULL,
  `pdef` decimal(5,0) default NULL,
  `matk` decimal(5,0) default NULL,
  `mdef` decimal(5,0) default NULL,
  `atkspd` decimal(3,0) default NULL,
  `aggro` decimal(6,0) default NULL,
  `matkspd` decimal(4,0) default NULL,
  `rhand` decimal(4,0) default NULL,
  `lhand` decimal(4,0) default NULL,
  `armor` decimal(1,0) default NULL,
  `walkspd` decimal(3,0) default NULL,
  `runspd` decimal(3,0) default NULL,
  `faction_id` varchar(40) default NULL,
  `faction_range` decimal(4,0) default NULL,
  `isUndead` int(11) default 0,
  `absorb_level` decimal(2,0) default 0,
  `absorb_type` enum('FULL_PARTY','LAST_HIT','PARTY_ONE_RANDOM') DEFAULT 'LAST_HIT' NOT NULL,
  PRIMARY KEY (`id`)
);

-- SPAWN LIST
DROP TABLE IF EXISTS `spawnlist`;
CREATE TABLE `spawnlist` (
  `id` int(11) NOT NULL auto_increment,
  `location` varchar(40) NOT NULL default '',
  `count` int(9) NOT NULL default '0',
  `npc_templateid` int(9) NOT NULL default '0',
  `locx` int(9) NOT NULL default '0',
  `locy` int(9) NOT NULL default '0',
  `locz` int(9) NOT NULL default '0',
  `randomx` int(9) NOT NULL default '0',
  `randomy` int(9) NOT NULL default '0',
  `heading` int(9) NOT NULL default '0',
  `respawn_delay` int(9) NOT NULL default '0',
  `loc_id` int(9) NOT NULL default '0',
  `periodOfDay` decimal(2,0) default '0'
  );
  
  
 -- ETC ITEM
 DROP TABLE IF EXISTS `etcitem`;
CREATE TABLE `etcitem` (
  `item_id` decimal(11,0) NOT NULL DEFAULT '0',
  `name` varchar(100) DEFAULT NULL,
  `crystallizable` varchar(5) DEFAULT NULL,
  `item_type` varchar(12) DEFAULT NULL,
  `weight` decimal(4,0) DEFAULT NULL,
  `consume_type` varchar(9) DEFAULT NULL,
  `material` varchar(11) DEFAULT NULL,
  `crystal_type` varchar(4) DEFAULT NULL,
  `duration` decimal(3,0) DEFAULT NULL,
  `price` decimal(11,0) DEFAULT NULL,
  `crystal_count` int(4) DEFAULT NULL,
  `sellable` varchar(5) DEFAULT NULL,
  `dropable` varchar(5) DEFAULT NULL,
  `destroyable` varchar(5) DEFAULT NULL,
  `tradeable` varchar(5) DEFAULT NULL,
  `id_name` varchar(100) NOT NULL DEFAULT '',
  `drop_category` enum('0','1','2') NOT NULL DEFAULT '2',
  PRIMARY KEY (`item_id`)
);

-- LVLUPGAIN
DROP TABLE IF EXISTS `LVLUPGAIN`;
CREATE TABLE `LVLUPGAIN` (
  `classid` int(3) NOT NULL default '0',
  `defaulthpbase` decimal(5,1) NOT NULL default '0.0',
  `defaulthpadd` decimal(4,2) NOT NULL default '0.00',
  `defaulthpmod` decimal(4,2) NOT NULL default '0.00',
  `defaultcpbase` decimal(5,1) NOT NULL default '0.0',
  `defaultcpadd` decimal(4,2) NOT NULL default '0.00',
  `defaultcpmod` decimal(4,2) NOT NULL default '0.00',
  `defaultmpbase` decimal(5,1) NOT NULL default '0.0',
  `defaultmpadd` decimal(4,2) NOT NULL default '0.00',
  `defaultmpmod` decimal(4,2) NOT NULL default '0.00',
  `class_lvl` int(3) NOT NULL default '0',
  PRIMARY KEY  (`classid`)
);

-- NPC
DROP TABLE IF EXISTS `npc`;
CREATE TABLE `npc` (
  `id` decimal(11,0) NOT NULL default '0',
  `idTemplate` int(11) NOT NULL default '0',
  `name` varchar(200) default NULL,
  `serverSideName` int(1) default '0',
  `title` varchar(45) default '',
  `serverSideTitle` int(1) default '0',
  `class` varchar(200) default NULL,
  `collision_radius` decimal(10,2) default NULL,
  `collision_height` decimal(10,2) default NULL,
  `level` decimal(2,0) default NULL,
  `sex` varchar(6) default NULL,
  `type` varchar(20) default NULL,
  `attackrange` decimal(10,2) default NULL,
  `hp` decimal(8,0) default NULL,
  `mp` decimal(5,0) default NULL,
  `hpreg` decimal(8,2) default NULL,
  `mpreg` decimal(5,2) default NULL,
  `str` decimal(7,0) default NULL,
  `con` decimal(7,0) default NULL,
  `dex` decimal(7,0) default NULL,
  `int` decimal(7,0) default NULL,
  `wit` decimal(7,0) default NULL,
  `men` decimal(7,0) default NULL,
  `exp` decimal(9,0) default NULL,
  `sp` decimal(8,0) default NULL,
  `patk` decimal(5,0) default NULL,
  `pdef` decimal(5,0) default NULL,
  `matk` decimal(5,0) default NULL,
  `mdef` decimal(5,0) default NULL,
  `atkspd` decimal(3,0) default NULL,
  `aggro` decimal(10,2) default NULL,
  `matkspd` decimal(4,0) default NULL,
  `rhand` decimal(4,0) default NULL,
  `lhand` decimal(4,0) default NULL,
  `armor` decimal(1,0) default NULL,
  `walkspd` decimal(10,2) default NULL,
  `runspd` decimal(10,2) default NULL,
  `faction_id` varchar(40) default NULL,
  `faction_range` decimal(10,2) default NULL,
  `isUndead` int(11) default 0,
  `absorb_level` decimal(2,0) default 0,
  `absorb_type` enum('FULL_PARTY','LAST_HIT','PARTY_ONE_RANDOM') DEFAULT 'LAST_HIT' NOT NULL,
  PRIMARY KEY (`id`)
);

-- SPAWNLIST
DROP TABLE IF EXISTS `spawnlist`;
CREATE TABLE `spawnlist` (
  `id` int(11) NOT NULL auto_increment,
  `location` varchar(40) NOT NULL default '',
  `count` int(9) NOT NULL default '0',
  `npc_templateid` int(9) NOT NULL default '0',
  `locx` int(9) NOT NULL default '0',
  `locy` int(9) NOT NULL default '0',
  `locz` int(9) NOT NULL default '0',
  `randomx` int(9) NOT NULL default '0',
  `randomy` int(9) NOT NULL default '0',
  `heading` int(9) NOT NULL default '0',
  `respawn_delay` int(9) NOT NULL default '0',
  `loc_id` int(9) NOT NULL default '0',
  `periodOfDay` decimal(2,0) default '0');

-- WEAPON
DROP TABLE IF EXISTS `weapon`;
CREATE TABLE `weapon` (
  `item_id` decimal(11,0) NOT NULL default '0',
  `name` varchar(70) default NULL,
  `bodypart` varchar(15) default NULL,
  `crystallizable` varchar(5) default NULL,
  `weight` decimal(4,0) default NULL,
  `soulshots` decimal(2,0) default NULL,
  `spiritshots` decimal(1,0) default NULL,
  `material` varchar(11) default NULL,
  `crystal_type` varchar(4) default NULL,
  `p_dam` decimal(5,0) default NULL,
  `rnd_dam` decimal(2,0) default NULL,
  `weaponType` varchar(8) default NULL,
  `critical` decimal(2,0) default NULL,
  `hit_modify` decimal(6,5) default NULL,
  `avoid_modify` decimal(2,0) default NULL,
  `shield_def` decimal(3,0) default NULL,
  `shield_def_rate` decimal(2,0) default NULL,
  `atk_speed` decimal(3,0) default NULL,
  `mp_consume` decimal(2,0) default NULL,
  `m_dam` decimal(3,0) default NULL,
  `duration` decimal(3,0) default NULL,
  `price` decimal(11,0) default NULL,
  `crystal_count` int(4) default NULL,
  `sellable` varchar(5) default NULL,
  `dropable` varchar(5) default NULL,
  `destroyable` varchar(5) default NULL,
  `tradeable` varchar(5) default NULL,
  PRIMARY KEY  (`item_id`)
);
