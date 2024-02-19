package com.shnok.javaserver.db.entity;

import com.shnok.javaserver.model.Point3D;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "SPAWNLIST")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SpawnList {
    /*
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
      `periodOfDay` decimal(2,0) default '0',
      PRIMARY KEY  (id),
      KEY `key_npc_templateid` (`npc_templateid`)
    ) ENGINE=MyISAM;
    */

    public SpawnList(int id, String location, int count, int npcId, float posX, float posY, float posZ, float randomX,
                     float randomY, float heading, int respawnDelay, int locId, float periodOfDay) {
        this.id = id;
        this.location = location;
        this.count = count;
        this.npcId = npcId;
        this.posX = posX;
        this.posY = posY;
        this.posZ = posZ;
        this.randomX = randomX;
        this.randomY = randomY;
        this.heading = heading;
        this.respawnDelay = respawnDelay;
        this.locId = locId;
        this.periodOfDay = periodOfDay;
    }

    @Column
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column
    private String location;
    @Column
    private int count;
    @Column(name = "npc_templateid")
    private int npcId;
    @Column(name = "locx")
    private float posX;
    @Column(name = "locy")
    private float posY;
    @Column(name = "locz")
    private float posZ;
    @Column(name = "randomx")
    private float randomX;
    @Column(name = "randomy")
    private float randomY;
    @Column
    private float heading;
    @Column(name = "respawn_delay")
    private int respawnDelay;
    @Column(name = "loc_id")
    private int locId;
    @Column
    private float periodOfDay;

    @Transient
    private Point3D spawnPosition;

    public Point3D getSpawnPosition() {
        if(spawnPosition == null) {
            spawnPosition = new Point3D(posX, posY, posZ);
        }
        return new Point3D(spawnPosition);
    }
}
