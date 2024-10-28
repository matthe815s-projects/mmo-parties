package dev.matthe815.mmoparties.common.networking.builders;

import com.google.common.base.Charsets;
import dev.matthe815.mmoparties.common.gui.PartyList;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;

public class BuilderWaypoint implements BuilderData {
    String name;
    int x;
    int y;
    int z;

    @Override
    public void OnWrite(ByteBuf buffer, EntityPlayer player) {
        buffer.writeInt(player.getName().length());
        buffer.writeCharSequence(player.getName(), Charsets.UTF_8);
        buffer.writeInt(player.getPosition().getX());
        buffer.writeInt(player.getPosition().getY());
        buffer.writeInt(player.getPosition().getZ());
    }

    @Override
    public void OnRead(ByteBuf buffer) {
        name = (String)buffer.readCharSequence(buffer.readInt(), Charsets.UTF_8);
        x = buffer.readInt();
        y = buffer.readInt();
        z = buffer.readInt();
    }

    @Override
    public boolean IsDifferent(EntityPlayer player) {
        return player.getPosition().getX() != x || player.getPosition().getY() != y || player.getPosition().getZ() != z;
    }

    public static class NuggetBar implements PartyList.NuggetBar {
        @Override
        public int Render(BuilderData data, int xOffset, int yOffset, boolean compact) {
            BuilderWaypoint builder = (BuilderWaypoint) data;
            BlockPos targetPos = new BlockPos(builder.x, builder.y, builder.z);

            getPlayerLookVector(targetPos, builder.name);
            return 0;
        }

        public void getPlayerLookVector(BlockPos targetPos, String name) {
            Minecraft mc = Minecraft.getMinecraft();

            double x = targetPos.getX();
            double y = targetPos.getY(); // Adjust height as needed
            double z = targetPos.getZ();

            // Get the player's camera position
            double cameraX = mc.getRenderViewEntity().posX;
            double cameraY = mc.getRenderViewEntity().posY;
            double cameraZ = mc.getRenderViewEntity().posZ;

            // Calculate the difference
            double deltaX = x - cameraX;
            double deltaY = y - cameraY;
            double deltaZ = z - cameraZ;

            // Project the position to screen coordinates
            // Get the player's yaw (rotation)
            float yaw = (float) Math.toRadians(mc.player.getPitchYaw().x);
            float pitch = (float) Math.toRadians(mc.player.getPitchYaw().y);

            // Calculate distance to the target player
            double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);
            if (distance < 10) return;

            // Initialize screen position
            ScaledResolution resolution = new ScaledResolution(Minecraft.getMinecraft());
            int screenX = resolution.getScaledWidth() / 2;
            int screenY = resolution.getScaledHeight() / 2;

            // Calculate the forward direction vector
            double forwardX = -Math.sin(yaw);
            double forwardZ = Math.cos(yaw);

            // Calculate the dot product to determine if the target is in front or behind
            double dotProduct = deltaX * forwardX + deltaZ * forwardZ;
            String text = name;
            int textWidth = mc.fontRenderer.getStringWidth(text);

            // General positioning logic
            screenX = (int) ((resolution.getScaledWidth() / 2) - (deltaX * Math.cos(yaw) + deltaZ * Math.sin(yaw)) * (100 / Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ)));
            screenY = (int) ((resolution.getScaledHeight() / 2) - (deltaY * (100 / distance)) - (Math.sin(pitch) * 50)); // Inverted Y based on pitch

            // Adjust for snapping to bottom if facing away
            if (dotProduct < 0) {
                screenY = resolution.getScaledHeight() - 10; // Snap to bottom edge
            }

            // Ensure screenX and screenY are within the screen bounds
            screenX = Math.max(10, Math.min(resolution.getScaledWidth() - 10 - textWidth, screenX));
            screenY = Math.max(10, Math.min(resolution.getScaledHeight() - 10, screenY));

            // Draw the text
            Minecraft.getMinecraft().fontRenderer.drawString(text, screenX, screenY, 0xFFFFFF);
        }
    }
}
