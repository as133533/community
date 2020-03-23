package life.hrx.weibo.cache;


import life.hrx.weibo.dto.TagDTO;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


//这个类的主要目的就是用来有数据库一般的功能，因为就几个固定的标签，也不允许用户进行修改和增加，所以就直接在cache中存储，还有筛选出不符合条件的tags
public class TagCache {

    //返回所有我们自定义的tagDTO数组
    public static List<TagDTO> get(){
        List<TagDTO> tagDTOS=new ArrayList<>();
        TagDTO program=new TagDTO();
        program.setCategoryName("开发语言");
        program.setTags(Arrays.asList("javascript", "php", "css", "html", "html5", "java", "node.js", "python", "c++", "c", "golang", "objective-c", "typescript", "shell", "swift", "c#", "sass", "ruby", "bash", "less", "asp.net", "lua", "scala", "coffeescript", "actionscript", "rust", "erlang", "perl"));
        tagDTOS.add(program);

        TagDTO framework = new TagDTO();
        framework.setCategoryName("平台框架");
        framework.setTags(Arrays.asList("laravel", "spring", "express", "django", "flask", "yii", "ruby-on-rails", "tornado", "koa", "struts"));
        tagDTOS.add(framework);


        TagDTO server = new TagDTO();
        server.setCategoryName("服务器");
        server.setTags(Arrays.asList("linux", "nginx", "docker", "apache", "ubuntu", "centos", "缓存 tomcat", "负载均衡", "unix", "hadoop", "windows-server"));
        tagDTOS.add(server);

        TagDTO db = new TagDTO();
        db.setCategoryName("数据库");
        db.setTags(Arrays.asList("mysql", "redis", "mongodb", "sql", "oracle", "nosql memcached", "sqlserver", "postgresql", "sqlite"));
        tagDTOS.add(db);

        TagDTO tool = new TagDTO();
        tool.setCategoryName("开发工具");
        tool.setTags(Arrays.asList("git", "github", "visual-studio-code", "vim", "sublime-text", "xcode intellij-idea", "eclipse", "maven", "ide", "svn", "visual-studio", "atom emacs", "textmate", "hg"));
        tagDTOS.add(tool);

        return tagDTOS;
    }

    //用来挑选出前端用户输入的tags中跟我们自定义的tags不匹配的部分，返还给用户显示
    public static String filterInvalid(String tags){
        //将tags按,分离成数组
        String[] split = StringUtils.split(tags,",");

        //获得所有的tags组成的数组
        List<TagDTO> tagDTOS = get();

        //flatmap将一个流中对应的每一个流都收集到一个流中，然后进行操作，而map是对流中的每个元素进行操作，然后返回操作后的元素。
        List<String> tagslist = tagDTOS.stream().flatMap(tagDTO -> tagDTO.getTags().stream()).collect(Collectors.toList());//返回所有的标签组成的集合

        //将split中与collect中不同的挑选出来，使用joining往其中添加
        String invalid = Arrays.stream(split).filter(StringUtils::isNotBlank).filter(s ->  !tagslist.contains(s)).collect(Collectors.joining(","));
        return invalid;


    }

}
