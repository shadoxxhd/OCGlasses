package com.bymarcin.openglasses.gui;

import com.bymarcin.openglasses.event.minecraft.client.ClientKeyboardEvents;
import com.bymarcin.openglasses.network.NetworkRegistry;
import com.bymarcin.openglasses.network.packet.GlassesEventPacket;
import com.bymarcin.openglasses.surface.OCClientSurface;
import com.bymarcin.openglasses.utils.GlassesInstance;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Keyboard;

import java.io.IOException;

import static ben_mkiv.rendertoolkit.surface.ClientSurface.vec3d000;

public class InteractGui extends GuiScreen {
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks){}

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if(OCClientSurface.glasses.get().isEmpty()) return;

        for(GlassesInstance.HostClient host : OCClientSurface.glasses.getHosts().values())
            if(host.sendOverlayEvents) {
                double x = mouseX, y = mouseY;
                if(!OCClientSurface.instance().getRenderResolution(host.uuid).equals(vec3d000)){
                    x*=(OCClientSurface.instance().getRenderResolution(host.uuid).x / OCClientSurface.resolution.getScaledWidth());
                    y*=(OCClientSurface.instance().getRenderResolution(host.uuid).y / OCClientSurface.resolution.getScaledHeight());
                }
                NetworkRegistry.packetHandler.sendToServer(new GlassesEventPacket(host.uuid, GlassesEventPacket.EventType.INTERACT_OVERLAY, (int) Math.round(x), (int) Math.round(y), mouseButton));
            }
    }

    @Override
    public void updateScreen() {
        if(!Keyboard.isKeyDown(ClientKeyboardEvents.interactGUIKey.getKeyCode())){
            OCClientSurface.glasses.getConditions().setOverlay(false);

            for(GlassesInstance.HostClient host : OCClientSurface.glasses.getHosts().values())
                if(host.sendOverlayEvents)
                    NetworkRegistry.packetHandler.sendToServer(new GlassesEventPacket(host.uuid, GlassesEventPacket.EventType.DEACTIVATE_OVERLAY));

            mc.displayGuiScreen(null);
        }
    }
}
