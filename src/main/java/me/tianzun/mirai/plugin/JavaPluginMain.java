package me.tianzun.mirai.plugin;

import net.mamoe.mirai.console.plugin.jvm.JavaPlugin;
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription;
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescriptionBuilder;
import net.mamoe.mirai.event.GlobalEventChannel;
import net.mamoe.mirai.event.events.FriendMessageEvent;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.data.*;
import net.mamoe.mirai.utils.ExternalResource;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * 使用 Java 请把
 * {@code /src/main/resources/META-INF.services/net.mamoe.mirai.console.plugin.jvm.JvmPlugin}
 * 文件内容改成 {@code org.example.mirai.plugin.JavaPluginMain} <br/>
 * 也就是当前主类全类名
 *
 * 使用 Java 可以把 kotlin 源集删除且不会对项目有影响
 *
 * 在 {@code settings.gradle.kts} 里改构建的插件名称、依赖库和插件版本
 *
 * 在该示例下的 {@link JvmPluginDescription} 修改插件名称，id 和版本等
 *
 * 可以使用 {@code src/test/kotlin/RunMirai.kt} 在 IDE 里直接调试，
 * 不用复制到 mirai-console-loader 或其他启动器中调试
 */

public final class JavaPluginMain extends JavaPlugin {
    private static JavaPluginMain main;
    public static JavaPluginMain getMain() { return main; }

    public static final JavaPluginMain INSTANCE = new JavaPluginMain();
    private JavaPluginMain() {
        super(new JvmPluginDescriptionBuilder("org.example.mirai-example", "0.1.0")
                .name("tz")
                .author("Tian_Zun_")
                .build());
    }

    @Override
    public void onEnable() {
        main = this;
        getLogger().info("tz >> Enable");
        FileClass.load();
        GlobalEventChannel.INSTANCE.subscribeAlways(GroupMessageEvent.class,event -> {
            MessageChain chain = event.getMessage();
            String text = Objects.requireNonNull(chain.get(PlainText.Key)).contentToString();
            if(text.contains("！jrrp") || text.contains("!jrrp") || text.contains("！今日人品") || text.contains("!今日人品")){
                //引用回复
                QuoteReply quoteReply = new QuoteReply(event.getSource());

                Map<String, List<String>> data = FileClass.getData();
                String qq = String.valueOf(event.getSender().getId());
                getLogger().info("receive:" + qq);
                String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
                getLogger().info("get date:" + date);
                getLogger().info("get qqdata:" + data.get(qq));
                if(!data.containsKey(qq) || !data.get(qq).get(1).equalsIgnoreCase(date)){
                    String rp = String.valueOf(new Random().nextInt(101));
                    List<String> list = Arrays.asList(rp, date);
                    data.put(qq,list);
                    FileClass.setData(data);
                    FileClass.save();
                }
                try {
                    URL url = new URL("https://acg.toubiec.cn/random.php");
                    BufferedImage bufferedImage = ImageIO.read(url);
                    ByteArrayOutputStream byteArrayOutputStream;
                    byteArrayOutputStream = new ByteArrayOutputStream();
                    ImageIO.write(bufferedImage,"jpg",byteArrayOutputStream);
                    byteArrayOutputStream.flush();
                    Image image =  event.getSubject().uploadImage(ExternalResource.create(byteArrayOutputStream.toByteArray()));
                    byteArrayOutputStream.close();

                    event.getSubject().sendMessage(MessageUtils.newChain(
                            (Message) quoteReply
                                    .plus( new At( event.getSender().getId() ) )
                                    .plus( "今日人品值为:"+data.get(qq).get(0) )
                                    .plus(image)
                            )
                    );
                    return;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                event.getSubject().sendMessage(MessageUtils.newChain(
                        (Message) quoteReply
                                .plus( new At( event.getSender().getId() ) )
                                .plus( "今日人品值为:"+data.get(qq).get(0) )
                        )
                );
            }
        });

        GlobalEventChannel.INSTANCE.subscribeAlways(FriendMessageEvent.class,event -> {
            if(event.getFriend().getId() != 2867411796L){
                return;
            }
           String msg = event.getMessage().get(PlainText.Key).toString();
            if(msg.equalsIgnoreCase("#data")){
                event.getSubject().sendMessage(FileClass.getData().toString().replace("],","]" + System.getProperty("line.separator")));
            }
            else if(msg.startsWith("#set")){
                msg = msg.replace("#set","");
                String[] arg = msg.split(",");
                String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
                List<String> list = Arrays.asList(arg[1], date);

                FileClass.setrp(arg[0],list);
                FileClass.save();
                event.getSubject().sendMessage("成功将"+arg[0]+"的今日人品值改为："+arg[1]);
            }
        });
    }
}
