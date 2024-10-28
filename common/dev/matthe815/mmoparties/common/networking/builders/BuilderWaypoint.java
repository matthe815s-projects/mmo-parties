package dev.matthe815.mmoparties.common.networking.builders;

import com.google.common.base.Charsets;
import dev.matthe815.mmoparties.common.gui.PartyList;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;

public class BuilderWaypoint implements BuilderData {
    String name;
    int x;
    int y;
    int z;

    @Override
    public void OnWrite(ByteBuf buffer, Player player) {
        buffer.writeInt(player.getName().getString().length());
        buffer.writeCharSequence(player.getName().getString(), Charsets.UTF_8);
        buffer.writeInt(player.getBlockX());
        buffer.writeInt(player.getBlockY());
        buffer.writeInt(player.getBlockZ());
    }

    @Override
    public void OnRead(ByteBuf buffer) {
        name = (String)buffer.readCharSequence(buffer.readInt(), Charsets.UTF_8);
        x = buffer.readInt();
        y = buffer.readInt();
        z = buffer.readInt();
    }

    @Override
    public boolean IsDifferent(Player player) {
        return player.getBlockX() != x || player.getBlockY() != y || player.getBlockZ() != z;
    }

    public static class NuggetBar implements PartyList.NuggetBar {
        @Override
        public int Render(GuiGraphics gui, BuilderData data, int xOffset, int yOffset, boolean compact) {
            BuilderWaypoint builder = (BuilderWaypoint) data;
            BlockPos targetPos = new BlockPos(builder.x, builder.y, builder.z);

            getPlayerLookVector(gui, targetPos, builder.name);
            return 0;
        }

        public void getPlayerLookVector(GuiGraphics gui, BlockPos targetPos, String name) {
            Minecraft mc = Minecraft.getInstance();

            double x = targetPos.getX();
            double y = targetPos.getY(); // Adjust height as needed
            double z = targetPos.getZ();

            // Get the player's camera position
            double cameraX = mc.getEntityRenderDispatcher().camera.getPosition().x;
            double cameraY = mc.getEntityRenderDispatcher().camera.getPosition().y;
            double cameraZ = mc.getEntityRenderDispatcher().camera.getPosition().z;

            // Calculate the difference
            double deltaX = x - cameraX;
            double deltaY = y - cameraY;
            double deltaZ = z - cameraZ;

            // Project the position to screen coordinates
            // Get the player's yaw (rotation)
            float yaw = (float) Math.toRadians(mc.player.getYRot());
            float pitch = (float) Math.toRadians(mc.player.getXRot());

            // Calculate distance to the target player
            double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);
            if (distance < 10) return;

            // Initialize screen position
            int screenX = mc.getWindow().getGuiScaledWidth() / 2;
            int screenY = mc.getWindow().getGuiScaledHeight() / 2;

            // Calculate the forward direction vector
            double forwardX = -Math.sin(yaw);
            double forwardZ = Math.cos(yaw);

            // Calculate the dot product to determine if the target is in front or behind
            double dotProduct = deltaX * forwardX + deltaZ * forwardZ;
            String text = name;
            int textWidth = mc.font.width(text);

            // General positioning logic
            screenX = (int) ((mc.getWindow().getGuiScaledWidth() / 2) - (deltaX * Math.cos(yaw) + deltaZ * Math.sin(yaw)) * (100 / Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ)));
            screenY = (int) ((mc.getWindow().getGuiScaledHeight() / 2) - (deltaY * (100 / distance)) - (Math.sin(pitch) * 50)); // Inverted Y based on pitch

            // Adjust for snapping to bottom if facing away
            if (dotProduct < 0) {
                screenY = mc.getWindow().getGuiScaledHeight() - 10; // Snap to bottom edge
            }

            // Ensure screenX and screenY are within the screen bounds
            screenX = Math.max(10, Math.min(mc.getWindow().getGuiScaledWidth() - 10 - textWidth, screenX));
            screenY = Math.max(10, Math.min(mc.getWindow().getGuiScaledHeight() - 10, screenY));

            // Draw the text
            gui.drawString(mc.font, text, screenX, screenY, 0xFFFFFF);
        }
    }
}
