package com.shnok.javaserver.pathfinding;

import com.shnok.javaserver.Config;
import com.shnok.javaserver.model.Point3D;
import com.shnok.javaserver.pathfinding.node.Node;
import com.shnok.javaserver.util.ByteUtils;
import javolution.util.FastMap;
import lombok.extern.log4j.Log4j2;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Map;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

@Log4j2
public class GeodataLoader {
    private static GeodataLoader instance;
    public static GeodataLoader getInstance() {
        if (instance == null) {
            instance = new GeodataLoader();
        }
        return instance;
    }

    public Map<Point3D, Node> loadGeodataForMap(String mapId) {
        return loadFromFile(getGeodataFilePath(mapId), mapId);
    }

    private String getGeodataFilePath(String mapId) {
        return "geodata/" + mapId + ".geodata";
    }

    private Map<Point3D, Node> loadFromFile(String path, String mapId) {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(Objects.requireNonNull(classLoader.getResource(path)).getFile());

        // Read zip file (geodata file container)
        try (ZipFile zipFile = new ZipFile(file)) {
            Enumeration<? extends ZipEntry> entries = zipFile.entries();

            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                if (entry.getName().equals("geodata")) {
                    try (InputStream inputStream = zipFile.getInputStream(entry)) {
                        DataInputStream dataInputStream = new DataInputStream(inputStream);
                        return readGeodataFile(dataInputStream, mapId);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private Map<Point3D, Node> readGeodataFile(DataInputStream dis, String mapId) throws IOException {
        Map<Point3D, Node> geodata = new FastMap<>();

        int count = 0;
        try {
            while (true) {
                short posX = ByteUtils.swapShort(dis.readShort());
                short posY = ByteUtils.swapShort(dis.readShort());
                short posZ = ByteUtils.swapShort(dis.readShort());
                count++;

                Point3D nodeIndex = new Point3D(posX, posY, posZ);
                Point3D nodeWorldPos = Geodata.getInstance().fromNodeToWorldPos(nodeIndex, mapId);
                Node n = new Node(nodeIndex, nodeWorldPos, Config.NODE_SIZE);

                geodata.put(nodeIndex, n);
            }
        } catch (Exception e) {
        }

        log.info("Loaded {} node(s).", count);
        return geodata;
    }
}
