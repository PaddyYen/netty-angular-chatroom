package com.demo.entity;

import java.util.Random;

public class RandomName {
	public String getRandomName(){
        String[] nameList = {"石榴姐","陳小春","毛利小五郎","黑寡婦","徐長卿","天下","玫瑰花的葬禮","唐唐","八戒",
        		"唐老鴨","日向寧次","只想一生跟你走","兔八哥","郭靖","卡卡西","宋國民","風清揚","葉孤城" ,"胖虎","科比",
        		"張啟山","陸毅","張藝興","哆啦A夢","陳赫","李大嘴","文章","尹新月","週芷若","你的背包","嘆服","小智",
        		"聖誕結","大頭兒子","白展堂","時光隧道","陳皮阿四","如果愛下去","謝霆鋒","遇見","怒放的生命","慢慢", "情書",
        		"李逍遙","喜歡你","張國榮","黑崎一護","藤原拓海","吳尊","南宮翎","犬夜叉","華萊士","好久不見", "我愛羅",
        		"夕日紅","紫萱","吳亦凡","喬丹","飛得更高","煙花易冷","許褚","包租公","唐雪見","卡布達","何潤東","八神太一",
        		"發如雪","龜仙人","施瓦辛格","韋德","三國殺","城府","明天過後","馬超","灰色頭像","天津飯", "萬有引力",
        		"一笑奈何","奈良鹿丸","邁克","景天","史努比","二喜","春野櫻","德萊文","潘森","李易峰" ,"胡歌","呂子喬",
        		"碧瑤","關羽","龍貓","王語嫣","王力宏","孫悟飯","馬天宇","真的愛你","愛情轉移","佟大為","真水無香",
        		"給我一首歌","小黑","手心的薔薇","白玫瑰","越前龍馬","秋道丁次","海闊天空","楊洋","沈佳宜","伊魯卡",
        		"若曦","小酒窩","稻香","宇智波鼬","李連杰","漩渦鳴人","單車","櫻木花道","灰原哀","逆戰","紳士","李洛克",
        		"馬里奧","背對背擁抱","楚雲飛","千里之外","雷小冉","江南","怪盜基德","同桌的你","斷橋殘雪" ,"劉在石",
        		"張翰","彭于晏","路飛","鷹眼","孫悟天","李白","大蛇丸","秦明","托尼","魏晨","石田大和", "蛤蟆吉","自來也",
        		"詹姆斯","桐華","花海","小夫","曹操","苦笑","三井壽","陸雪琪","一起搖擺","郭富城","流氓兔", "馬丁",
        		"夜空中最亮的星","宇智波佐助","冰河","不二週助","這,就是愛","冬獅郎","你若成風","浩克","楊紫曦","米老鼠",
        		"蘇菲","喬峰","二月紅","醉赤壁","高飛","幻聽","蓋倫","光輝歲月","厄加特","清明雨上","黑貓警長","秋香","阿童木",
        		"春天裡","霍建華","野比大雄","期待愛","任泉","秦羽墨","天天","千百度","小孩","孔明","聽媽媽的話" ,"黃軒",
        		"蜘蛛俠","告白氣球","不分手的戀愛","蘆葦微微","宙斯","阿木木","波比","井博然","鄧超","米粒儿" ,"凱南","柯南",
        		"赤丸","藍采和","唐伯虎","晴兒","最美的太陽","小星星","包租婆","佐井","安妮","楊過","貝吉塔","蝙蝠俠","菊花台",
        		"周杰倫","愛新覺羅·胤禛","十年","吻別","言承旭","重樓","特蘭克斯","徐崢","加菲貓","田靈兒","鋼鐵俠","西門吹雪",
        		"KO","麥兜","小寶","宮水四葉","雅典娜","認真的雪","歲月如歌","庫林","餓狼傳說","素顏","一千年以後","我懷念的",
        		"段譽","東風破","曹光","肖奈","姚明","K歌之王","成龍","浮誇","宮城","木婉清","安迪","風度","安西教練","黃忠",
        		"佈歐","韋小寶","終於等到你","蠟筆小新","日向雛田","劍心","撒旦","他不懂","林驚羽","鄭爽","工藤新一",
        		"你是我的眼","張小凡","七里香","木葉丸","薩滿","殺生丸","楓","索隆","鹿晗","趙雲", "星矢","五五開","張飛",
        		"悟空","沐之晴","趙靈兒","黃曉明","紫龍","皮卡丘","櫻桃小丸子","蘇有朋","拉克絲","李青","花火" ,"流川楓",
        		"虛竹","愛德華","周星馳","薇恩","海綿寶寶","綱手","鄭愷","有點甜","再見","看月亮爬上來","認錯","劉備","畫心",
        		"泡沫","有何不可","薛之謙","雙截棍","羅志祥","梅長蘇","凱","令狐衝","龍捲風","小熊維尼","何書桓" ,"學不會",
        		"小頭爸爸","卡卡洛特","你把我灌醉","明明就","比克","德瑪西亞","金鍾國","美國隊長","曾小賢", "宮水三葉","吳邪",
        		"傑尼龜","超夢","柯震東","趙本山","吳鎮宇","權志龍","陳偉霆","鍾漢良","郭敬明","陳奕迅","陳學冬","趙又廷",
        		"傑森·斯坦森","小瀋陽","你的名字","釋小龍","林書豪","吳彥祖","井柏然"};
        String name = nameList[new Random().nextInt(nameList.length)];
        return name;
    }
}
