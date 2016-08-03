package me.nentify.titles.config;

import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Config {

    private ConfigurationLoader<CommentedConfigurationNode> loader;
    private CommentedConfigurationNode config;

    public String hostname;
    public int port;
    public String database;
    public String username;
    public String password;

    public Config(Path configPath) throws IOException {
        loader = HoconConfigurationLoader.builder().setPath(configPath).build();

        if (!Files.exists(configPath))
            Files.createFile(configPath);

        config = loader.load();

        check("hostname", "localhost", "Hostname for your MySQL database");
        check("port", 3306, "Port for your MySQL database");
        check("database", "minecraft", "Database name");
        check("username", "root", "MySQL username");
        check("password", "password", "MySQL password");

        loader.save(config);

        hostname = config.getNode("hostname").getString();
        port = config.getNode("port").getInt();
        database = config.getNode("database").getString();
        username = config.getNode("username").getString();
        password = config.getNode("password").getString();
    }

    public void check(String node, Object defaultValue, String comment) {
        if (config.getNode(node).isVirtual())
            config.getNode(node).setValue(defaultValue).setComment(comment);
    }
}
