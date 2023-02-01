package me.wolfie.methane.mixin;

import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ProgressScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ApiServices;
import net.minecraft.util.UserCache;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.io.File;
import java.util.concurrent.Executor;

@Mixin(MinecraftClient.class)
public abstract class ClientPopUpMixin {


    @Shadow
    private ClientWorld world;
    @Shadow
    private boolean integratedServerRunning;
    @Shadow
    private YggdrasilAuthenticationService authenticationService;
    @Shadow
    private File runDirectory;


    /**
     * @author
     * @reason
     */
    /* /// removed for now, its a little unessecary as of now
    @Overwrite
    public void joinWorld(ClientWorld world) {
        ProgressScreen progressScreen = new ProgressScreen(true);
        progressScreen.setTitle(Text.translatable("connect.joining"));
            this.reset(progressScreen);
        progressScreen.setDone();
        this.world = world;
        this.setWorld(world);
        if (!this.integratedServerRunning) {
            ApiServices apiServices = ApiServices.create(this.authenticationService, this.runDirectory);
            apiServices.userCache().setExecutor((Executor) this);
            SkullBlockEntity.setServices(apiServices, (Executor) this);
            UserCache.setUseRemote(false);
        }
    }
    */

    @Shadow
    abstract void reset(Screen screen);

    @Shadow
    protected abstract void setWorld(ClientWorld world);



}
