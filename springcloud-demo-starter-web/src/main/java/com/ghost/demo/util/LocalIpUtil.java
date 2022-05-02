package com.ghost.demo.util;

import com.google.common.collect.Sets;
import lombok.NoArgsConstructor;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Set;

/**
 * @program dubbo-demo
 * @description:
 * @author: jackchow
 * @create: 2021/11/29 20:14
 */
@NoArgsConstructor
public class LocalIpUtil {
    public static Set<String> priorityLocal = Sets.newHashSet("eth", "bond", "en");
    private static volatile String localIp = null;
    private static Set<String> localIps = Sets.newHashSet();


    public static String getLocalIp() {
        if (localIp != null) {
            return localIp;
        } else {
            try {
                Enumeration<NetworkInterface> allNetInterface = NetworkInterface.getNetworkInterfaces();
                ArrayList list = new ArrayList();

                String priority;
                while (allNetInterface.hasMoreElements()) {
                    NetworkInterface e = allNetInterface.nextElement();

                    for (Enumeration address = e.getInetAddresses(); address.hasMoreElements(); localIps.add(priority)) {
                        InetAddress add = (InetAddress) address.nextElement();
                        priority = add.getHostAddress();
                        if (priority.contains("%")) {
                            priority = priority.substring(0, priority.indexOf("%"));
                        }
                    }

                    list.add(e);
                }

                Iterator var7 = list.iterator();

                NetworkInterface networkInterface;
                while (var7.hasNext()) {
                    networkInterface = (NetworkInterface) var7.next();
                    Iterator var9 = priorityLocal.iterator();

                    while (var9.hasNext()) {
                        priority = (String) var9.next();
                        if (networkInterface.getName().startsWith(priority) && getLocalIpInner(networkInterface)) {
                            return localIp;
                        }
                    }
                }

                var7 = list.iterator();

                do {
                    if (!var7.hasNext()) {
                        return null;
                    }

                    networkInterface = (NetworkInterface) var7.next();
                } while (!getLocalIpInner(networkInterface));

                return localIp;
            } catch (SocketException var6) {
                throw new RuntimeException(var6);
            }
        }
    }

    private static boolean getLocalIpInner(NetworkInterface networkInterface) {
        Enumeration address = networkInterface.getInetAddresses();

        while (address.hasMoreElements()) {
            InetAddress ip = (InetAddress) address.nextElement();
            if (ip instanceof Inet4Address) {
                String hostAddress = ip.getHostAddress();
                if (hostAddress != null && hostAddress.trim().length() != 0 && !hostAddress.startsWith("127") &&
                        !hostAddress.startsWith("192.168")) {
                    localIp = hostAddress;
                    return true;
                }
            }
        }
        return false;
    }
}

