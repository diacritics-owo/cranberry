package diacritics.owo.config;

import diacritics.owo.Cranberry;
import io.wispforest.owo.config.annotation.Config;
import io.wispforest.owo.config.annotation.Hook;
import io.wispforest.owo.config.annotation.Modmenu;

@Modmenu(modId = Cranberry.MOD_ID)
@Config(name = "cranberry-client-config", wrapperName = "ClientConfig")
public class ClientConfigModel {
  @Hook
  public boolean sendStatus = true;

  @Hook
  public boolean receiveStatus = true;

  public boolean colorBackground = true;
}
