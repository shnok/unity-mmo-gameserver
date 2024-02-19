-- 
-- Table structure for table `weapon`
-- 

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
  `item_skill_id` decimal(11,0) NOT NULL default '0',
  `item_skill_lvl` decimal(11,0) NOT NULL default '0',
  `enchant4_skill_id` decimal(11,0) NOT NULL default '0',
  `enchant4_skill_lvl` decimal(11,0) NOT NULL default '0',
  `onCast_skill_id` decimal(11,0) NOT NULL default '0',
  `onCast_skill_lvl` decimal(11,0) NOT NULL default '0',
  `onCast_skill_chance` decimal(11,0) NOT NULL default '0',
  `onCrit_skill_id` decimal(11,0) NOT NULL default '0',
  `onCrit_skill_lvl` decimal(11,0) NOT NULL default '0',
  `onCrit_skill_chance` decimal(11,0) NOT NULL default '0',
  PRIMARY KEY  (`item_id`)
);

-- 
-- Dumping data for table `weapon`
-- 

INSERT INTO `weapon` VALUES 
 ('6','Apprentice''s Wand','rhand','false','1350','1','1','steel','none','5','20','blunt','4','4.00000','0','0','0','379','0','7','-1','138','0','false','false','true','false','0','0','0','0','0','0','0','0','0','0'),
 ('14','Bow','lrhand','false','1930','1','1','wood','none','23','5','bow','12','-3.00000','0','0','0','293','1','9','-1','12500','0','true','true','true','true','0','0','0','0','0','0','0','0','0','0'),
 ('20','Buckler','lhand','false','1410','0','0','wood','none','0','0','none','0','0.00000','-8','67','20','0','0','0','-1','2780','0','true','true','true','true','0','0','0','0','0','0','0','0','0','0'),
 ('177','Mage Staff','lrhand','false','1050','2','2','wood','none','30','20','bigblunt','4','4.00000','0','0','0','325','0','28','-1','244000','0','true','true','true','true','0','0','0','0','0','0','0','0','0','0'),
 ('2369','Squire''s Sword','rhand','false','1600','1','1','steel','none','6','10','sword','8','0.00000','0','0','0','379','0','5','-1','138','0','false','false','true','false','0','0','0','0','0','0','0','0','0','0');