package com.shnok;

public class Shutdown extends Thread {
    private static Shutdown _instance;
    public static Shutdown getInstance()
    {
        if (_instance == null)
        {
            _instance = new Shutdown();
        }
        return _instance;
    }

    @Override
    public void run()
    {
        try {
            Server.getInstance().getGameServerListener().interrupt();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            for(GameClient c : Server.getInstance().getAllClients()) {
                c.interrupt();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            ThreadPoolManager.getInstance().shutdown();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
