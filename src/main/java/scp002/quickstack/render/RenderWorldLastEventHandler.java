package scp002.quickstack.render;

import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class RenderWorldLastEventHandler {

    public static final RenderWorldLastEventHandler INSTANCE = new RenderWorldLastEventHandler();

    @SubscribeEvent
    public void onRenderWorldLastEvent(RenderWorldLastEvent event) {
        RendererCube.INSTANCE.tryToRender(event);
    }

}
