package com.shnok.javaserver.model.stats;

import com.shnok.javaserver.model.object.entity.Entity;
import lombok.extern.log4j.Log4j2;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;

import static com.shnok.javaserver.config.Configuration.server;

@Log4j2
public enum BaseStats {
    STR(new STR()),
    INT(new INT()),
    DEX(new DEX()),
    WIT(new WIT()),
    CON(new CON()),
    MEN(new MEN()),
    NONE(new NONE());

    public static final int MAX_STAT_VALUE = 100;

    protected static final double[] STRbonus = new double[MAX_STAT_VALUE];
    protected static final double[] INTbonus = new double[MAX_STAT_VALUE];
    protected static final double[] DEXbonus = new double[MAX_STAT_VALUE];
    protected static final double[] WITbonus = new double[MAX_STAT_VALUE];
    protected static final double[] CONbonus = new double[MAX_STAT_VALUE];
    protected static final double[] MENbonus = new double[MAX_STAT_VALUE];

    private final BaseStat _stat;

    public final String getValue() {
        return _stat.getClass().getSimpleName();
    }

    BaseStats(BaseStat s) {
        _stat = s;
    }

    public final double calcBonus(Entity actor) {
        if (actor != null) {
            return _stat.calcBonus(actor);
        }

        return 1;
    }

    private interface BaseStat {
        double calcBonus(Entity actor);
    }

    protected static final class STR implements BaseStat {
        @Override
        public double calcBonus(Entity actor) {
            return STRbonus[actor.getSTR()];
        }
    }

    protected static final class INT implements BaseStat {
        @Override
        public double calcBonus(Entity actor) {
            return INTbonus[actor.getINT()];
        }
    }

    protected static final class DEX implements BaseStat {
        @Override
        public double calcBonus(Entity actor) {
            return DEXbonus[actor.getDEX()];
        }
    }

    protected static final class WIT implements BaseStat {
        @Override
        public double calcBonus(Entity actor) {
            return WITbonus[actor.getWIT()];
        }
    }

    protected static final class CON implements BaseStat {
        @Override
        public double calcBonus(Entity actor) {
            return CONbonus[actor.getCON()];
        }
    }

    protected static final class MEN implements BaseStat {
        @Override
        public double calcBonus(Entity actor) {
            return MENbonus[actor.getMEN()];
        }
    }

    protected static final class NONE implements BaseStat {
        @Override
        public double calcBonus(Entity actor) {
            return 1f;
        }
    }

    static {
        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setValidating(false);
        factory.setIgnoringComments(true);
        final File file = new File(server.datapackRoot(), "data/stats/statBonus.xml");
        Document doc = null;

        if (file.exists()) {
            try {
                doc = factory.newDocumentBuilder().parse(file);
            } catch (Exception e) {
                log.warn("[BaseStats] Could not parse file: " + e.getMessage(), e);
            }

            if (doc != null) {
                String statName;
                int val;
                double bonus;
                NamedNodeMap attrs;
                for (Node list = doc.getFirstChild(); list != null; list = list.getNextSibling()) {
                    if ("list".equalsIgnoreCase(list.getNodeName())) {
                        for (Node stat = list.getFirstChild(); stat != null; stat = stat.getNextSibling()) {
                            statName = stat.getNodeName();
                            for (Node value = stat.getFirstChild(); value != null; value = value.getNextSibling()) {
                                if ("stat".equalsIgnoreCase(value.getNodeName())) {
                                    attrs = value.getAttributes();
                                    try {
                                        val = Integer.parseInt(attrs.getNamedItem("value").getNodeValue());
                                        bonus = Double.parseDouble(attrs.getNamedItem("bonus").getNodeValue());
                                    } catch (Exception e) {
                                        log.error("[BaseStats] Invalid stats value: " + value.getNodeValue() + ", skipping");
                                        continue;
                                    }

                                    if ("STR".equalsIgnoreCase(statName)) {
                                        STRbonus[val] = bonus;
                                    } else if ("INT".equalsIgnoreCase(statName)) {
                                        INTbonus[val] = bonus;
                                    } else if ("DEX".equalsIgnoreCase(statName)) {
                                        DEXbonus[val] = bonus;
                                    } else if ("WIT".equalsIgnoreCase(statName)) {
                                        WITbonus[val] = bonus;
                                    } else if ("CON".equalsIgnoreCase(statName)) {
                                        CONbonus[val] = bonus;
                                    } else if ("MEN".equalsIgnoreCase(statName)) {
                                        MENbonus[val] = bonus;
                                    } else {
                                        log.error("[BaseStats] Invalid stats name: " + statName + ", skipping");
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            throw new Error("[BaseStats] File not found: " + file.getAbsolutePath());
        }
    }
}
