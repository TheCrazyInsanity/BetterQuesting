package betterquesting.api2.client.gui.controls;

import java.util.List;
import betterquesting.api2.client.gui.resources.colors.GuiColorStatic;
import betterquesting.api2.client.gui.resources.colors.IGuiColor;
import org.lwjgl.input.Mouse;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.SoundEvents;
import betterquesting.api2.client.gui.events.PEventBroadcaster;
import betterquesting.api2.client.gui.events.types.PEventButton;
import betterquesting.api2.client.gui.misc.IGuiRect;
import betterquesting.api2.client.gui.resources.textures.IGuiTexture;
import betterquesting.api2.client.gui.themes.presets.PresetColor;
import betterquesting.api2.client.gui.themes.presets.PresetTexture;

public class PanelButton implements IPanelButton
{
	private final IGuiRect transform;
	
	private final IGuiTexture[] texStates = new IGuiTexture[3];
	private IGuiColor[] colStates = new IGuiColor[]{new GuiColorStatic(128, 128, 128, 255), new GuiColorStatic(255, 255, 255, 255), new GuiColorStatic(16777120)};
	private IGuiTexture texIcon = null;
	private List<String> tooltip = null;
	private boolean txtShadow = true;
	private String btnText = "";
	private int btnState = 1;
	private int btnID = -1;
	
	private boolean pendingRelease = false;
	
	public PanelButton(IGuiRect rect, int id, String txt)
	{
		this.transform = rect;
		this.btnText = txt;
		this.btnID = id;
		
		this.setTextures(PresetTexture.BTN_NORMAL_0.getTexture(), PresetTexture.BTN_NORMAL_1.getTexture(), PresetTexture.BTN_NORMAL_2.getTexture());
		this.setTextHighlight(PresetColor.BTN_DISABLED.getColor(), PresetColor.BTN_IDLE.getColor(), PresetColor.BTN_HOVER.getColor());
	}
	
	public PanelButton setTextHighlight(IGuiColor disabled, IGuiColor idle, IGuiColor hover)
	{
		this.colStates[0] = disabled;
		this.colStates[1] = idle;
		this.colStates[2] = hover;
		return this;
	}
	
	public PanelButton setTextShadow(boolean enabled)
	{
		this.txtShadow = enabled;
		return this;
	}
	
	public PanelButton setTextures(IGuiTexture disabled, IGuiTexture idle, IGuiTexture hover)
	{
		this.texStates[0] = disabled;
		this.texStates[1] = idle;
		this.texStates[2] = hover;
		return this;
	}
	
	public PanelButton setIcon(IGuiTexture icon)
	{
		this.texIcon = icon;
		return this;
	}
	
	public PanelButton setTooltip(List<String> tooltip)
	{
		this.tooltip = tooltip;
		return this;
	}
	
	public void setText(String text)
	{
		this.btnText = text;
	}
	
	public String getText()
	{
		return this.btnText;
	}
	
	@Override
	public int getButtonID()
	{
		return this.btnID;
	}
	
	public int getButtonState()
	{
		return this.btnState;
	}
	
	@Override
	public boolean isEnabled()
	{
		return this.btnState > 0;
	}
	
	@Override
	public void setEnabled(boolean state)
	{
		this.btnState = state? 1 : 0;
	}
	
	@Override
	public IGuiRect getTransform()
	{
		return transform;
	}
	
	@Override
	public void initPanel()
	{
	}
	
	@Override
	public void drawPanel(int mx, int my, float partialTick)
	{
		IGuiRect bounds = this.getTransform();
		GlStateManager.pushMatrix();
		GlStateManager.color(1F, 1F, 1F, 1F);
		int curState = !isEnabled()? 0 : (bounds.contains(mx, my)? 2 : 1);
		
		if(curState == 2 && Mouse.isButtonDown(0) && pendingRelease)
		{
			curState = 0;
		}
		
		IGuiTexture t = texStates[curState];
		
		if(t != null) // Support for text or icon only buttons in one or more states.
		{
			t.drawTexture(bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight(), 0F, partialTick);
		}
		
		if(texIcon != null)
		{
			int isz = Math.min(bounds.getHeight(), bounds.getWidth());
			texIcon.drawTexture(bounds.getX() + (bounds.getWidth()/2) - (isz/2), bounds.getY() + (bounds.getHeight()/2) - (isz/2), isz, isz, 0F, partialTick);
		}
		
		if(btnText != null && btnText.length() > 0)
		{
			drawCenteredString(Minecraft.getMinecraft().fontRenderer, btnText, bounds.getX() + bounds.getWidth()/2, bounds.getY() + bounds.getHeight()/2 - 4, colStates[btnState].getRGB(), txtShadow);
		}
		
		GlStateManager.popMatrix();
	}
	
    private static void drawCenteredString(FontRenderer font, String text, int x, int y, int color, boolean shadow)
    {
        font.drawString(text, x - font.getStringWidth(text) / 2, y, color, shadow);
    }
    
    /*private static void drawString(FontRenderer font, String text, int x, int y, int color, boolean shadow)
    {
        font.drawString(text, x, y, color, shadow);
    }*/
    
	@Override
	public boolean onMouseClick(int mx, int my, int click)
	{
		IGuiRect bounds = this.getTransform();
		pendingRelease = isEnabled() && click == 0 && bounds.contains(mx, my);

		return pendingRelease;
	}
	
	@Override
	public boolean onMouseRelease(int mx, int my, int click)
	{
		if(!pendingRelease)
		{
			return false;
		}

		pendingRelease = false;

		IGuiRect bounds = this.getTransform();
		boolean clicked = isEnabled() && click == 0 && bounds.contains(mx, my) && !PEventBroadcaster.INSTANCE.postEvent(new PEventButton(this));
		
		if(clicked)
		{
	        Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
		}
		
		return clicked;
	}
	
	@Override
	public boolean onMouseScroll(int mx, int my, int scroll)
	{
		return false;
	}
	
	@Override
	public boolean onKeyTyped(char c, int keycode)
	{
		return false;
	}
	
	@Override
	public List<String> getTooltip(int mx, int my)
	{
		if(this.getTransform().contains(mx, my))
		{
			return tooltip;
		}
		
		return null;
	}
}
