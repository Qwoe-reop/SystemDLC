package dev.mark.system.net;

import dev.mark.system.core.ClientMain;
import dev.mark.system.enums.PacketDirection;
import dev.mark.system.event.PacketEvent;
import dev.mark.system.module.Module;
import dev.mark.system.util.TPSTracker;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkSide;
import net.minecraft.network.packet.Packet;

public class CustomClientConnection extends ClientConnection {
    private static volatile boolean sendingSilent = false;
    private static volatile boolean receivingSilent = false;

    public CustomClientConnection(NetworkSide networkside) {
        super(networkside);
    }

    protected void method_10770(ChannelHandlerContext channelhandlercontext, Packet<?> packet) {
        if (!receivingSilent) {
            PacketEvent packetevent = new PacketEvent(packet, PacketDirection.RECEIVE);
            this.fireEvent(packetevent);
            if (packetevent.isCancelled()) {
                return;
            }
        }

        super.method_10770(channelhandlercontext, packet);
    }

    public void method_10743(Packet<?> packet) {
        if (!sendingSilent) {
            PacketEvent packetevent = new PacketEvent(packet, PacketDirection.SEND);
            this.fireEvent(packetevent);
            if (packetevent.isCancelled()) {
                return;
            }
        }

        super.method_10743(packet);
    }

    private void fireEvent(PacketEvent packetevent) {
        try {
            TPSTracker.getInstance().onPacket(packetevent);
            if (ClientMain.getInstance() != null && ClientMain.getInstance().getModuleManager() != null) {
                for (Module module : ClientMain.getInstance().getModuleManager().getEnabledModules()) {
                    try {
                        module.onPacket(packetevent);
                        if (packetevent.isCancelled()) {
                            break;
                        }
                    } catch (Exception var5) {
                    }
                }
            }
        } catch (Exception var6) {
        }
    }

    public static void setSendingSilent(boolean flag) {
        sendingSilent = flag;
    }

    public static boolean isSendingSilent() {
        return sendingSilent;
    }

    public static void setReceivingSilent(boolean flag) {
        receivingSilent = flag;
    }

    public static boolean isReceivingSilent() {
        return receivingSilent;
    }
}
