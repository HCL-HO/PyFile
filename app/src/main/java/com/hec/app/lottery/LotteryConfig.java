package com.hec.app.lottery;

import java.util.Map;
import java.util.HashMap;

/**
 * Created by jhezenhu on 2017/4/14.
 */

public final class LotteryConfig {
    public static final class PLAY_MODE {
        public static final int CLASSIC = 1;    // 经典
        public static final int EXPERT  = 2;    // 专家
    }

    public static final class LOTTERY_ID {
        public static final int CHONGQING_REALTIME      = 1;    // 重庆时时彩
        public static final int WELFARE_LOTTERY_3D      = 3;    // 福彩3D
        public static final int SPORTS_LOTTERY          = 4;    // 体彩排列三
        public static final int GUANGDONG_SELECT_FIVE   = 6;    // 广东十一选五
        public static final int SHANDONG_SELECT_FIVE    = 11;   // 山东十一选五
        public static final int BEIJING_PK10            = 12;   // 北京PK拾
        public static final int HS_REALTIME             = 13;   // 聚星时时彩
        public static final int HS_SELECT_FIVE          = 14;   // 聚星十一选五
        public static final int XINJIANG_REALTIME       = 15;   // 新疆时时彩
        public static final int HS_SF_REAMTIME          = 16;   // 聚星三分彩
        public static final int HS_PK10                 = 17;   // 聚星PK拾
        public static final int JIANG_SU_KUAI_SAN       = 18;   // 江苏快三
        public static final int HS_MMC                  = 19;   // 聚星秒秒彩
        public static final int HS_MMC_PK10             = 20;   // PK拾秒秒彩
        public static final int TIANJIN_REALTIME        = 21;   // 天津时时彩
        public static final int TAIWAN_WF_REALTIME      = 23;   // 台湾5分彩
        public static final int BEIJING_KENO            = 24;   // 北京快乐8
        public static final int KOREA_WF_REALTIME       = 25;   // 韩国5分彩
        public static final int GERMANY_PK10            = 26;   // 德国PK拾
        public static final int QQ_REAMTIME             = 27;   // 腾讯分分彩
        public static final int ITALY_PK10              = 28;   // 意大利PK拾
        public static final int ITALY_REAMTIME          = 29;   // 意大利分分彩
        public static final int REAL_MAN                = 100;  // 真人娱乐
        public static final int SLOT                    = 123;  // 聚星老虎机
        public static final int BAIJIALE                = 124;  // 多人游戏
        public static final int CHATROOM_BJL            = 125;  // 聊天室百家乐
        public static final int BAIJIALES               = 126;  // 多人百家乐
        public static final int FISHING                 = 127;  // 聚星捕鱼王

        public static final Map<String, Integer> LOTTERY_ID_MAP = new HashMap<String, Integer>(){{
            put("重庆时时彩", CHONGQING_REALTIME);
            put("福彩3D", WELFARE_LOTTERY_3D);
            put("体彩排列三", SPORTS_LOTTERY);
            put("广东十一选五", GUANGDONG_SELECT_FIVE);
            put("山东十一选五", SHANDONG_SELECT_FIVE);
            put("北京PK拾", BEIJING_PK10);
            put("聚星时时彩", HS_REALTIME);
            put("聚星十一选五", HS_SELECT_FIVE);
            put("新疆时时彩", XINJIANG_REALTIME);
            put("聚星三分彩", HS_SF_REAMTIME);
            put("聚星PK拾", HS_PK10);
            put("江苏快三", JIANG_SU_KUAI_SAN);
            put("聚星秒秒彩", HS_MMC);
            put("PK拾秒秒彩", HS_MMC_PK10);
            put("天津时时彩", TIANJIN_REALTIME);
            put("台湾5分彩", TAIWAN_WF_REALTIME);
            put("北京快乐8", BEIJING_KENO);
            put("韩国5分彩", KOREA_WF_REALTIME);
            put("德国PK拾", GERMANY_PK10);
            put("腾讯分分彩", QQ_REAMTIME);
            put("意大利PK拾", ITALY_PK10);
            put("意大利分分彩", ITALY_REAMTIME);
            put("真人娱乐", REAL_MAN);
            put("聚星老虎机", SLOT);
            put("多人游戏", BAIJIALE);
            put("聊天室百家乐", CHATROOM_BJL);
            put("多人3D百家乐", BAIJIALES);
            put("聚星捕鱼王", FISHING);
        }};
    }

    public static final class REAL_TIME {
        // Play Type Id
        public static final int FIVE_STAR                       = 1;    // 五星
        public static final int FOUR_STAR                       = 2;    // 四星
        public static final int BACK_THREE                      = 3;    // 后三
        public static final int CENTER_THREE                    = 4;    // 中三
        public static final int FRONT_THREE                     = 5;    // 前三
        public static final int BACK_TWO                        = 6;    // 后二
        public static final int FRONT_TWO                       = 7;    // 前二
        public static final int SPECIFIC_POSITIONING            = 8;    // 定位胆
        public static final int UNSPECIFIC_POSITIONING          = 9;    // 不定位
        public static final int BIG_SMALL_ODD_EVEN              = 10;   // 大小单双
        public static final int OPTIONAL_TWO                    = 11;   // 任二
        public static final int OPTIONAL_THREE                  = 12;   // 任三
        public static final int OPTIONAL_FOUR                   = 13;   // 任四
        public static final int DRAGON_TIGER_TIE                = 14;   // 龙虎和
        public static final int OPTIONAL                        = 15;   // 任选
        public static final int GROUP_SELECTION_MIXED_SINGLE    = 16;   // 组选混合单式
        public static final Map<String, Integer> PLAY_TYPE_MAP = new HashMap<String, Integer>(){{
            put("五星", FIVE_STAR);
            put("四星", FOUR_STAR);
            put("后三", BACK_THREE);
            put("中三", CENTER_THREE);
            put("前三", FRONT_THREE);
            put("后二", BACK_TWO);
            put("前二", FRONT_TWO);
            put("定位胆", SPECIFIC_POSITIONING);
            put("不定位", UNSPECIFIC_POSITIONING);
            put("大小单双", BIG_SMALL_ODD_EVEN);
            put("任二", OPTIONAL_TWO);
            put("任三", OPTIONAL_THREE);
            put("任四", OPTIONAL_FOUR);
            put("龙虎和", DRAGON_TIGER_TIE);
            put("任选", OPTIONAL);
            put("组选混合单式", GROUP_SELECTION_MIXED_SINGLE);
        }};

        // Play Type Redio Id
        public static final int DIRECTION_DUPLEX                = 1;    // 直选复式
        public static final int DIRECTION_SINGLE                = 2;    // 直选单式
        public static final int DIRECTION_SUM                   = 3;    // 直选和值
        public static final int DIRECTION_SPAN                  = 4;    // 直选跨度
        public static final int FIVE_COMBINATION                = 5;    // 五星组合
        public static final int FOUR_COMBINATION                = 6;    // 四星组合
        public static final int BACK_THREE_COMBINATION          = 7;    // 后三组合
        public static final int CENTER_THREE_COMBINATION        = 8;    // 中三組合
        public static final int FRONT_THREE_COMBINATION         = 9;    // 前三组合
        public static final int GROUP_THREE_DUPLEX              = 10;    // 组三复式
        public static final int GROUP_THREE_SINGLE              = 11;   // 组三单式
        public static final int GROUP_SIX_DUPLEX                = 12;   // 组六复式
        public static final int GROUP_SIX_SINGLE                = 13;   // 组六单式
        public static final int GROUP_SELECTION_DUPLEX          = 14;   // 组选复式
        public static final int GROUP_SELECTION_SINGLE          = 15;   // 组选单式
        public static final int MIXED_GROUP_SELECTION           = 16;   // 混合组选
        public static final int GROUP_SELECTION_SUM             = 17;   // 组选和值
        public static final int GROUP_SELECTION_TOWED           = 18;   // 组选包胆
        public static final int FOUR_STAR_GROUP_SELECTION       = 19;   // 四星组选
        public static final int GROUP_SELECTION_120             = 20;   // 组选120
        public static final int GROUP_SELECTION_60              = 21;   // 组选60
        public static final int GROUP_SELECTION_30              = 22;   // 组选30
        public static final int GROUP_SELECTION_24              = 23;   // 组选24
        public static final int GROUP_SELECTION_20              = 24;   // 组选20
        public static final int GROUP_SELECTION_12              = 25;   // 组选12
        public static final int GROUP_SELECTION_10              = 26;   // 组选10
        public static final int GROUP_SELECTION_5               = 27;   // 组选5
        public static final int GROUP_SELECTION_6               = 28;   // 组选6
        public static final int GROUP_SELECTION_4               = 29;   // 组选4
        public static final int SMOOTH_SAILING                  = 30;   // 一帆风顺
        public static final int GOOD_THINGS_IN_PAIRS            = 31;   // 好事成双
        public static final int SAN_XING_BAO_XI                 = 32;   // 三星报喜
        public static final int SI_JI_FA_CAI                    = 33;   // 四季发财
        public static final int SUM_MANTISSA                    = 34;   // 和值尾数
        public static final int SPECIAL_NUMBER                  = 35;   // 特殊号
        public static final int BACK_THREE_ONE_NUMBER           = 36;   // 后三一码
        public static final int FRONT_THREE_ONE_NUMBER          = 37;   // 前三一码
        public static final int BACK_THREE_TWO_NUMBER           = 38;   // 后三二码
        public static final int FRONT_THREE_TWO_NUMBER          = 39;   // 前三二码
        public static final int FOUR_STAR_ONE_NUMBER            = 40;   // 四星一码
        public static final int FOUR_STAR_TWO_NUMBER            = 41;   // 四星二码
        public static final int FIVE_STAR_TWO_NUMBER            = 42;   // 五星二码
        public static final int FIVE_STAR_THREE_NUMBER          = 43;   // 五星三码
        public static final int FRONT_TWO_BIG_SMALL_ODD_EVEN    = 44;   // 前二大小单双
        public static final int BACK_TWO_BIG_SMALL_ODD_EVEN     = 45;   // 后二大小单双
        public static final int FRONT_THREE_BIG_SMALL_ODD_EVEN  = 46;   // 前三大小单双
        public static final int BACK_THREE_BIG_SMALL_ODD_EVEN   = 47;   // 后三大小单双
        public static final int SUM_BIG_SMALL_ODD_EVEN          = 48;   // 总和大小单双
        public static final int OPTIONAL_FOUR_DUPLEX            = 49;   // 任选四复式
        public static final int OPTIONAL_FOUR_SINGLE            = 50;   // 任选四单式
        public static final int OPTIONAL_THREE_DUPLEX           = 51;   // 任选三复式
        public static final int OPTIONAL_THREE_SINGLE           = 52;   // 任选三单式
        public static final int OPTIONAL_TWO_DUPLEX             = 53;   // 任选二复式
        public static final int OPTIONAL_TWO_SINGLE             = 54;   // 任选二单式
        public static final int FRONT_THREE_MIXED_SINGLE        = 55;   // 前三混合单式
        public static final int CENTER_THREE_MIXED_SINGLE       = 56;   // 中三混合单式
        public static final int BACK_THREE_MIXED_SINGLE         = 57;   // 后三混合单式
        public static final Map<String, Integer> PLAY_TYPE_REDIO_MAP = new HashMap<String, Integer>(){{
            put("直选复式", DIRECTION_DUPLEX);
            put("直选单式", DIRECTION_SINGLE);
            put("直选和值", DIRECTION_SUM);
            put("直选跨度", DIRECTION_SPAN);
            put("五星组合", FIVE_COMBINATION);
            put("四星组合", FOUR_COMBINATION);
            put("后三组合", BACK_THREE_COMBINATION);
            put("中三组合", CENTER_THREE_COMBINATION);
            put("前三组合", FRONT_THREE_COMBINATION);
            put("组三复式", GROUP_THREE_DUPLEX);
            put("组三单式", GROUP_THREE_SINGLE);
            put("组六复式", GROUP_SIX_DUPLEX);
            put("组六单式", GROUP_SIX_SINGLE);
            put("混合组选", MIXED_GROUP_SELECTION);
            put("组选复式", GROUP_SELECTION_DUPLEX);
            put("组选单式", GROUP_SELECTION_SINGLE);
            put("组选和值", GROUP_SELECTION_SUM);
            put("组选包胆", GROUP_SELECTION_TOWED);
            put("四星组选", FOUR_STAR_GROUP_SELECTION);
            put("组选120", GROUP_SELECTION_120);
            put("组选60", GROUP_SELECTION_60);
            put("组选30", GROUP_SELECTION_30);
            put("组选24", GROUP_SELECTION_24);
            put("组选20", GROUP_SELECTION_20);
            put("组选12", GROUP_SELECTION_12);
            put("组选10", GROUP_SELECTION_10);
            put("组选5", GROUP_SELECTION_5);
            put("组选6", GROUP_SELECTION_6);
            put("组选4", GROUP_SELECTION_4);
            put("一帆风顺", SMOOTH_SAILING);
            put("好事成双", GOOD_THINGS_IN_PAIRS);
            put("三星报喜", SAN_XING_BAO_XI);
            put("四季发财", SI_JI_FA_CAI);
            put("和值尾数", SUM_MANTISSA);
            put("特殊号", SPECIAL_NUMBER);
            put("后三一码", BACK_THREE_ONE_NUMBER);
            put("前三一码", FRONT_THREE_ONE_NUMBER);
            put("后三二码", BACK_THREE_TWO_NUMBER);
            put("前三二码", FRONT_THREE_TWO_NUMBER);
            put("四星一码", FOUR_STAR_ONE_NUMBER);
            put("四星二码", FOUR_STAR_TWO_NUMBER);
            put("五星二码", FIVE_STAR_TWO_NUMBER);
            put("五星三码", FIVE_STAR_THREE_NUMBER);
            put("前二大小单双", FRONT_TWO_BIG_SMALL_ODD_EVEN);
            put("后二大小单双", BACK_TWO_BIG_SMALL_ODD_EVEN);
            put("前三大小单双", FRONT_THREE_BIG_SMALL_ODD_EVEN);
            put("后三大小单双", BACK_THREE_BIG_SMALL_ODD_EVEN);
            put("总和大小单双", SUM_BIG_SMALL_ODD_EVEN);
            put("任选四复式", OPTIONAL_FOUR_DUPLEX);
            put("任选四单式", OPTIONAL_FOUR_SINGLE);
            put("任选三复式", OPTIONAL_THREE_DUPLEX);
            put("任选三单式", OPTIONAL_THREE_SINGLE);
            put("任选二复式", OPTIONAL_TWO_DUPLEX);
            put("任选二单式", OPTIONAL_TWO_SINGLE);
            put("前三混合单式", FRONT_THREE_MIXED_SINGLE);
            put("中三混合单式", CENTER_THREE_MIXED_SINGLE);
            put("后三混合单式", BACK_THREE_MIXED_SINGLE);
        }};
    }

    public static final class PK_10 {
        // Play Type Id
        public static final int FIRST_ONE               = 1;    // 前一
        public static final int FIRST_TWO               = 2;    // 前二
        public static final int FIRST_THREE             = 3;    // 前三
        public static final int FIRST_FOUR              = 4;    // 前四
        public static final int FIRST_FIVE              = 5;    // 前五
        public static final int FIRST_SIX               = 6;    // 前六
        public static final int SPECIFIC_POSITIONING    = 7;    // 定位胆
        public static final int DRAGON_TIGER_FIGHT      = 8;    // 龙虎斗
        public static final int BIG_SMALL_ODD_EVEN      = 9;    // 大小单双
        public static final int GUAN_YA_SUM             = 10;   // 冠亚和
        public static final int SYNTHESIS               = 11;   // 综合
        public static final Map<String, Integer> PLAY_TYPE_MAP = new HashMap<String, Integer>(){{
            put("前一", FIRST_ONE);
            put("前二", FIRST_TWO);
            put("前三", FIRST_THREE);
            put("前四", FIRST_FOUR);
            put("前五", FIRST_FIVE);
            put("前六", FIRST_SIX);
            put("定位胆", SPECIFIC_POSITIONING);
            put("龙虎斗", DRAGON_TIGER_FIGHT);
            put("大小单双", BIG_SMALL_ODD_EVEN);
            put("冠亚和", GUAN_YA_SUM);
            put("综合", SYNTHESIS);
        }};

        // Play Type Redio Id
        public static final int FIRST_ONE_DUPLEX        = 1;    // 前一复式
        public static final int FIRST_TWO_DUPLEX        = 2;    // 前二复式
        public static final int FIRST_TWO_SINGLE        = 3;    // 前二单式
        public static final int FIRST_THREE_DUPLEX      = 4;    // 前三复式
        public static final int FIRST_THREE_SINGLE      = 5;    // 前三单式
        public static final int FIRST_FOUR_DUPLEX       = 6;    // 前四复式
        public static final int FIRST_FOUR_SINGLE       = 7;    // 前四单式
        public static final int FIRST_FIVE_DUPLEX       = 8;    // 前五复式
        public static final int FIRST_FIVE_SINGLE       = 9;    // 前五单式
        public static final int FIRST_SIX_DUPLEX        = 10;   // 前六复式
        public static final int FIRST_SIX_SINGLE        = 11;   // 前六单式
        public static final int FIXED_MULTIPLE          = 12;   // 定倍下单
        public static final int MANUAL_MULTIPLE         = 13;   // 手动倍数
        public static final int ONE_VS_TEN              = 14;   // 1-Vs-10
        public static final int TWO_VS_NINE             = 15;   // 2-Vs-9
        public static final int THREE_VS_EIGHT          = 16;   // 3-Vs-8
        public static final int FOUR_VS_SEVEN           = 17;   // 4-Vs-7
        public static final int FIVE_VS_SIX             = 18;   // 5-Vs-6
        public static final int CHAMPION                = 19;   // 冠军
        public static final int THE_FIRST_RUNNER_UP     = 20;   // 亚军
        public static final int THE_SECOND_RUNNER_UP    = 21;   // 季军
        public static final int FOURTH                  = 22;   // 第四名
        public static final int FIFTH                   = 23;   // 第五名
        public static final int SIXTH                   = 24;   // 第六名
        public static final int SEVENTH                 = 25;   // 第七名
        public static final int EIGHTH                  = 26;   // 第八名
        public static final int NINETH                  = 27;   // 第九名
        public static final int TENTH                   = 28;   // 第十名
        public static final Map<String, Integer> PLAY_TYPE_REDIO_MAP = new HashMap<String, Integer>(){{
            put("前一复式", FIRST_ONE_DUPLEX);
            put("前二复式", FIRST_TWO_DUPLEX);
            put("前二单式", FIRST_TWO_SINGLE);
            put("前三复式", FIRST_THREE_DUPLEX);
            put("前三复式", FIRST_THREE_SINGLE);
            put("前四复式", FIRST_FOUR_DUPLEX);
            put("前四单式", FIRST_FOUR_SINGLE);
            put("前五复式", FIRST_FIVE_DUPLEX);
            put("前五单式", FIRST_FIVE_SINGLE);
            put("前六复式", FIRST_SIX_DUPLEX);
            put("前六单式", FIRST_SIX_SINGLE);
            put("定倍下单", FIXED_MULTIPLE);
            put("手动倍数", MANUAL_MULTIPLE);
            put("1-Vs-10", ONE_VS_TEN);
            put("2-Vs-9", TWO_VS_NINE);
            put("3-Vs-8", THREE_VS_EIGHT);
            put("4-Vs-7", FOUR_VS_SEVEN);
            put("5-Vs-6", FIVE_VS_SIX);
            put("冠军", CHAMPION);
            put("亚军", THE_FIRST_RUNNER_UP);
            put("季军", THE_SECOND_RUNNER_UP);
            put("第四名", FOURTH);
            put("第五名", FIFTH);
            put("第六名", SIXTH);
            put("第七名", SEVENTH);
            put("第八名", EIGHTH);
            put("第九名", NINETH);
            put("第十名", TENTH);
        }};
    }

    public static final class SELECT_FILE {
        // Play Type Id
        public static final int THREE_NUMBER            = 1;    // 三码
        public static final int TWO_NUMBER              = 2;    // 二码
        public static final int UNSPECIFIC_POSITIONING  = 3;    // 不定位
        public static final int SPECIFIC_POSITIONING    = 4;    // 定位胆
        public static final int CARNIVAL                = 5;    // 趣味
        public static final int AT_WILL_DOUBLE_DUPLEX   = 6;    // 任选复式
        public static final int AT_WILL_DOUBLE_SINGLE   = 7;    // 任选单式
        public static final int AT_WILL_TOWED           = 8;    // 任选胆拖
        public static final Map<String, Integer> PLAY_TYPE_MAP = new HashMap<String, Integer>(){{
            put("三码", THREE_NUMBER);
            put("二码", TWO_NUMBER);
            put("不定位", UNSPECIFIC_POSITIONING);
            put("定位胆", SPECIFIC_POSITIONING);
            put("趣味型", CARNIVAL);
            put("任选复式", AT_WILL_DOUBLE_DUPLEX);
            put("任选单式", AT_WILL_DOUBLE_SINGLE);
            put("任选胆拖", AT_WILL_TOWED);
        }};

        // Play Type Redio Id
        public static final int FRONT_THREE_DIRECTION_DUPLEX        = 1;    // 前三直选复式
        public static final int FRONT_THREE_DIRECTION_SINGLE        = 2;    // 前三直选单式
        public static final int FRONT_THREE_GROUP_SELECTION_DUPLEX  = 3;    // 前三组选复式
        public static final int FRONT_THREE_GROUP_SELECTION_SINGLE  = 4;    // 前三组选单式
        public static final int FRONT_THREE_GROUP_SELECTION_TOWED   = 5;    // 前三组选胆拖
        public static final int FRONT_TWO_DIRECTION_DUPLEX          = 6;    // 前二直选复式
        public static final int FRONT_TWO_DIRECTION_SINGLE          = 7;    // 前二直选单式
        public static final int FRONT_TWO_GROUP_SELECTION_DUPLEX    = 8;    // 前二组选复式
        public static final int FRONT_TWO_GROUP_SELECTION_SINGLE    = 9;    // 前二组选单式
        public static final int FRONT_TWO_GROUP_SELECTION_TOWED     = 10;   // 前二组选胆拖
        public static final int FRONT_THREE_UNSPECIFIC_POSITIONING  = 11;   // 前三不定位
        public static final int FRONT_THREE_SPECIFIC_POSITIONING    = 12;   // 前三定位胆
        public static final int ODD_EVEN                            = 13;   // 定单双
        public static final int GUESS_MEDIAN                        = 14;   // 猜中位
        public static final int OPTIONAL_ONE_OF_ONE                 = 15;   // 任选一中一
        public static final int OPTIONAL_TWO_OF_TWO                 = 16;   // 任选二中二
        public static final int OPTIONAL_THREE_OF_THREE             = 17;   // 任选三中三
        public static final int OPTIONAL_FOUR_OF_FOUR               = 18;   // 任选四中四
        public static final int OPTIONAL_FIVE_OF_FIVE               = 19;   // 任选五中五
        public static final int OPTIONAL_FIVE_OF_SIX                = 20;   // 任选六中五
        public static final int OPTIONAL_FIVE_OF_SEVEN              = 21;   // 任选七中五
        public static final int OPTIONAL_FIVE_OF_EIGHT              = 22;   // 任选八中五
        public static final int ONE_OF_ONE                          = 23;   // 一中一
        public static final int TWO_OF_TWO                          = 24;   // 二中二
        public static final int THREE_OF_THREE                      = 25;   // 三中三
        public static final int FOUR_OF_FOUR                        = 26;   // 四中四
        public static final int FIVE_OF_FIVE                        = 27;   // 五中五
        public static final int FIVE_OF_SIX                         = 28;   // 六中五
        public static final int FIVE_OF_SEVEN                       = 29;   // 七中五
        public static final int FIVE_OF_EIGHT                       = 30;   // 八中五
        public static final Map<String, Integer> PLAY_TYPE_REDIO_MAP = new HashMap<String, Integer>(){{
            put("前三直选复式", FRONT_THREE_DIRECTION_DUPLEX);
            put("前三直选单式", FRONT_THREE_DIRECTION_SINGLE);
            put("前三组选复式", FRONT_THREE_GROUP_SELECTION_DUPLEX);
            put("前三组选单式", FRONT_THREE_GROUP_SELECTION_SINGLE);
            put("前三组选胆拖", FRONT_THREE_GROUP_SELECTION_TOWED);
            put("前二直选复式", FRONT_TWO_DIRECTION_DUPLEX);
            put("前二直选单式", FRONT_TWO_DIRECTION_SINGLE);
            put("前二组选复式", FRONT_TWO_GROUP_SELECTION_DUPLEX);
            put("前二组选单式", FRONT_TWO_GROUP_SELECTION_SINGLE);
            put("前二组选胆拖", FRONT_TWO_GROUP_SELECTION_TOWED);
            put("前三不定位", FRONT_THREE_UNSPECIFIC_POSITIONING);
            put("前三定位胆", FRONT_THREE_SPECIFIC_POSITIONING);
            put("定单双", ODD_EVEN);
            put("猜中位", GUESS_MEDIAN);
            put("任选一中一", OPTIONAL_ONE_OF_ONE);
            put("任选二中二", OPTIONAL_TWO_OF_TWO);
            put("任选三中三", OPTIONAL_THREE_OF_THREE);
            put("任选四中四", OPTIONAL_FOUR_OF_FOUR);
            put("任选五中五", OPTIONAL_FIVE_OF_FIVE);
            put("任选六中五", OPTIONAL_FIVE_OF_SIX);
            put("任选七中五", OPTIONAL_FIVE_OF_SEVEN);
            put("任选八中五", OPTIONAL_FIVE_OF_EIGHT);
            put("一中一", ONE_OF_ONE);
            put("二中二", TWO_OF_TWO);
            put("三中三", THREE_OF_THREE);
            put("四中四", FOUR_OF_FOUR);
            put("五中五", FIVE_OF_FIVE);
            put("六中五", FIVE_OF_SIX);
            put("七中五", FIVE_OF_SEVEN);
            put("八中五", FIVE_OF_EIGHT);
        }};
    }

    public static final class LOW_FREQUECE {
        // Play Type Id
        public static final int THREE_STAR                      = 1;    // 三星
        public static final int TWO_STAR                        = 2;    // 二星
        public static final int SPECIFIC_POSITIONING            = 3;    // 定位胆
        public static final int UNSPECIFIC_POSITIONING          = 4;    // 不定位
        public static final int BIG_SMALL_ODD_EVEN              = 5;    // 大小单双
        public static final int GROUP_SELECTION_MIXED_SINGLE    = 6;    // 组选混合单式
        public static final Map<String, Integer> PLAY_TYPE_MAP = new HashMap<String, Integer>(){{
            put("三星", THREE_STAR);
            put("二星", TWO_STAR);
            put("定位胆", SPECIFIC_POSITIONING);
            put("不定位", UNSPECIFIC_POSITIONING);
            put("大小单双", BIG_SMALL_ODD_EVEN);
            put("组选混合单式", GROUP_SELECTION_MIXED_SINGLE);
        }};

        // Play Type Redio Id
        public static final int DIRECTION_DUPLEX                    = 1;    // 直选复式
        public static final int DIRECTION_SINGLE                    = 2;    // 直选单式
        public static final int DIRECTION_SUM                       = 3;    // 直选和值
        public static final int GROUP_THREE_DUPLEX                  = 4;    // 组三复式
        public static final int GROUP_THREE_SINGLE                  = 5;    // 组三单式
        public static final int GROUP_SIX_DUPLEX                    = 6;    // 组六复式
        public static final int GROUP_SIX_SINGLE                    = 7;    // 组六单式
        public static final int MIXED_GROUP_SELECTION               = 8;    // 混合组选
        public static final int GROUP_SELECTION_SUM                 = 9;    // 组选和值
        public static final int FRONT_TWO_DIRECTION_DUPLEX          = 10;   // 前二直选复式
        public static final int FRONT_TWO_DIRECTION_SINGLE          = 11;   // 前二直选单式
        public static final int BACK_TWO_DIRECTION_DUPLEX           = 12;   // 后二直选复式
        public static final int BACK_TWO_DIRECTION_SINGLE           = 13;   // 后二直选单式
        public static final int FRONT_TWO_GROUP_SELECTION_DUPLEX    = 14;   // 前二组选复式
        public static final int FRONT_TWO_GROUP_SELECTION_SINGLE    = 15;   // 前二组选单式
        public static final int BACK_TWO_GROUP_SELECTION_DUPLEX     = 16;   // 后二组选复式
        public static final int BACK_TWO_GROUP_SELECTION_SINGLE     = 17;   // 后二组选单式
        public static final int ONE_NUMBER_UNSPECIFIC_POSITIONING   = 18;   // 一码不定位
        public static final int TWO_NUMBER_UNSPECIFIC_POSITIONING   = 19;   // 二码不定位
        public static final int FRONT_TWO_BIG_SMALL_ODD_EVEN        = 20;   // 前二大小单双
        public static final int BACK_TWO_BIG_SMALL_ODD_EVEN         = 21;   // 后二大小单双
        public static final int MIXED_SINGLE                        = 22;   // 混合单式
        public static final Map<String, Integer> PLAY_TYPE_REDIO_MAP = new HashMap<String, Integer>(){{
            put("直选复式", DIRECTION_DUPLEX);
            put("直选单式", DIRECTION_SINGLE);
            put("直选和值", DIRECTION_SUM);
            put("组三复式", GROUP_THREE_DUPLEX);
            put("组三单式", GROUP_THREE_SINGLE);
            put("组六复式", GROUP_SIX_DUPLEX);
            put("组六单式", GROUP_SIX_SINGLE);
            put("混合组选", MIXED_GROUP_SELECTION);
            put("组选和值", GROUP_SELECTION_SUM);
            put("前二直选复式", FRONT_TWO_DIRECTION_DUPLEX);
            put("前二直选单式", FRONT_TWO_DIRECTION_SINGLE);
            put("后二直选复式", BACK_TWO_DIRECTION_DUPLEX);
            put("后二直选单式", BACK_TWO_DIRECTION_SINGLE);
            put("前二组选复式", FRONT_TWO_GROUP_SELECTION_DUPLEX);
            put("前二组选单式", FRONT_TWO_GROUP_SELECTION_SINGLE);
            put("后二组选复式", BACK_TWO_GROUP_SELECTION_DUPLEX);
            put("后二组选单式", BACK_TWO_GROUP_SELECTION_SINGLE);
            put("一码不定位", ONE_NUMBER_UNSPECIFIC_POSITIONING);
            put("二码不定位", TWO_NUMBER_UNSPECIFIC_POSITIONING);
            put("前二大小单双", FRONT_TWO_BIG_SMALL_ODD_EVEN);
            put("后二大小单双", BACK_TWO_BIG_SMALL_ODD_EVEN);
            put("混合单式", MIXED_SINGLE);
        }};
    }

    public static final class UNIT {
        public static final int TEN_THOUSAND    = 0;
        public static final int THOUSAND        = 1;
        public static final int HUNDRED         = 2;
        public static final int TEN             = 3;
        public static final int ONE             = 4;

        public final static Map<String, Integer> UNIT_MAP = new HashMap<String, Integer>() {{
            put("个", ONE);
            put("十", TEN);
            put("百", HUNDRED);
            put("千", THOUSAND);
            put("万", TEN_THOUSAND);
        }};
    }
}
