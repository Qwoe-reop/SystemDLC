package dev.mark.system.net;

import dev.mark.system.gui.screen.CustomConnectScreen;
import java.net.InetSocketAddress;
import java.util.Optional;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.DisconnectedScreen;
import net.minecraft.client.network.Address;
import net.minecraft.client.network.AllowedAddressResolver;
import net.minecraft.client.network.ClientLoginNetworkHandler;
import net.minecraft.client.network.CookieStorage;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.network.ServerInfo.ResourcePackPolicy;
import net.minecraft.client.resource.server.ServerResourcePackManager.AcceptanceStatus;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkSide;
import net.minecraft.network.packet.c2s.login.LoginHelloC2SPacket;
import net.minecraft.network.state.LoginStates;
import net.minecraft.text.Text;

public class ServerConnectThread extends Thread {
    final ServerAddress val$address;
    final MinecraftClient val$client;
    final ServerInfo val$info;
    final CookieStorage val$cookieStorage;
    final CustomConnectScreen this$0;

    public ServerConnectThread(
        CustomConnectScreen CustomConnectScreen,
        String s,
        ServerAddress serveraddress,
        MinecraftClient minecraftclient,
        ServerInfo serverinfo,
        CookieStorage cookiestorage
    ) {
        super(s);
        this.this$0 = CustomConnectScreen;
        this.val$address = serveraddress;
        this.val$client = minecraftclient;
        this.val$info = serverinfo;
        this.val$cookieStorage = cookiestorage;
    }

    @Override
    public void run() {
        InetSocketAddress inetsocketaddress = null;

        try {
            if (!this.this$0.connectingCancelled) {
                Optional optional = AllowedAddressResolver.field_33745.method_36907(this.val$address).map(Address::method_36902);
                if (!this.this$0.connectingCancelled) {
                    if (optional.isEmpty()) {
                        this.val$client
                            .execute(
                                () -> this.val$client
                                    .method_1507(
                                        new DisconnectedScreen(this.this$0.parent, this.this$0.failureErrorMessage, CustomConnectScreen.UNKNOWN_HOST_TEXT)
                                    )
                            );
                    } else {
                        inetsocketaddress = (InetSocketAddress)optional.get();
                        CustomClientConnection customclientconnection;
                        synchronized (this.this$0) {
                            if (this.this$0.connectingCancelled) {
                                return;
                            }

                            customclientconnection = new CustomClientConnection(NetworkSide.field_11942);
                            customclientconnection.method_53505(this.val$client.method_53526().method_53544());
                            this.this$0.future = ClientConnection.method_52271(
                                inetsocketaddress, this.val$client.field_1690.method_1639(), customclientconnection
                            );
                        }

                        this.this$0.future.syncUninterruptibly();
                        synchronized (this.this$0) {
                            if (this.this$0.connectingCancelled) {
                                customclientconnection.method_10747(CustomConnectScreen.ABORTED_TEXT);
                                return;
                            }

                            this.this$0.connection = customclientconnection;
                            this.val$client.method_1516().method_55528(customclientconnection, toAcceptanceStatus(this.val$info.method_2990()));
                        }

                        this.this$0
                            .connection
                            .method_56326(
                                inetsocketaddress.getHostName(),
                                inetsocketaddress.getPort(),
                                LoginStates.field_48247,
                                LoginStates.field_48248,
                                new ClientLoginNetworkHandler(
                                    this.this$0.connection,
                                    this.val$client,
                                    this.val$info,
                                    this.this$0.parent,
                                    false,
                                    null,
                                    this.this$0::setStatus,
                                    this.val$cookieStorage
                                ),
                                this.val$cookieStorage != null
                            );
                        this.this$0
                            .connection
                            .method_10743(new LoginHelloC2SPacket(this.val$client.method_1548().method_1676(), this.val$client.method_1548().method_44717()));
                    }
                }
            }
        } catch (Exception exception2) {
            if (!this.this$0.connectingCancelled) {
                Exception exception;
                if (exception2.getCause() instanceof Exception exception1) {
                    exception = exception1;
                } else {
                    exception = exception2;
                }

                CustomConnectScreen.LOGGER.error("Couldn't connect SmallStateData server", exception2);
                String s2;
                if (inetsocketaddress == null) {
                    s2 = exception.getMessage();
                } else {
                    s2 = exception.getMessage();
                    int i = inetsocketaddress.getPort();
                    String s = inetsocketaddress.getHostName();
                    s2 = s2.replaceAll(s + ":" + i, "").replaceAll(inetsocketaddress.toString(), "");
                }

                String s1 = s2;
                this.val$client
                    .execute(
                        () -> this.val$client
                            .method_1507(
                                new DisconnectedScreen(
                                    this.this$0.parent, this.this$0.failureErrorMessage, Text.method_43469("disconnect.genericReason", new Object[]{s1})
                                )
                            )
                    );
            }
        }
    }

    private static AcceptanceStatus toAcceptanceStatus(ResourcePackPolicy resourcepackpolicy) {
        switch (resourcepackpolicy) {
            case field_3768:
                return AcceptanceStatus.field_47648;
            case field_3764:
                return AcceptanceStatus.field_47649;
            case field_3767:
                return AcceptanceStatus.field_47647;
            default:
                throw new MatchException(null, null);
        }
    }
}
