package dev.plex.rank.enums;

import dev.plex.util.PlexUtils;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.json.JSONObject;

@Getter
public enum Rank
{
    IMPOSTOR(-1, "<aqua>an <yellow>Impostor<reset>", "Impostor", "<dark_gray>[<yellow>Imp<dark_gray>]", NamedTextColor.YELLOW),
    NONOP(0, "a <white>Non-Op<reset>", "Non-Op", "", NamedTextColor.WHITE),
    OP(1, "an <green>Op<reset>", "Operator", "<dark_gray>[<green>OP<dark_gray>]", NamedTextColor.GOLD),
    ADMIN(2, "an <green>Admin<reset>", "Admin", "<dark_gray>[<green>Admin<dark_gray>]", NamedTextColor.GREEN),
    TELNET_ADMIN(3, "a <dark_green>Telnet Admin<reset>", "Super Telnet Admin", "<dark_gray>[<dark_green>STA<dark_gray>]", NamedTextColor.DARK_GREEN),
    SENIOR_ADMIN(4, "a <gold>Senior Admin<reset>", "Senior Admin", "<dark_gray>[<gold>SrA<dark_gray>]", NamedTextColor.GOLD),
    EXECUTIVE(5, "an <red>Executive<reset>", "Executive", "<dark_gray>[<red>Exec<dark_gray>]", NamedTextColor.RED),
    SYSTEM_ADMIN(6, "a <dark_purple>System Admin<reset>", "System Admin", "<dark_gray>[<dark_purple>Sys<dark_gray>]", NamedTextColor.DARK_PURPLE);
    

    private final int level;
    @Getter
    private final NamedTextColor color;
    @Setter
    private String loginMessage;
    @Setter
    private String readable;
    @Setter
    private String prefix;

    Rank(int level, String loginMessage, String readable, String prefix, NamedTextColor color)
    {
        this.level = level;
        this.loginMessage = loginMessage;
        this.readable = readable;
        this.prefix = prefix;
        this.color = color;
    }

    public boolean isAtLeast(Rank rank)
    {
        return this.level >= rank.getLevel();
    }

    public Component getPrefix()
    {
        return PlexUtils.mmDeserialize(this.prefix);
    }

    public JSONObject toJSON()
    {
        JSONObject object = new JSONObject();
        object.put("prefix", this.prefix);
        object.put("loginMessage", this.loginMessage);
        return new JSONObject().put(this.name(), object);
    }
}
