package com.sky.dto.baidu.map;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

@Data
public class BaiduMapDirectionLiteDTO {
    /**
     * 状态码
     * 0：成功
     * 1：服务内部错误
     * 2：参数无效
     * 7：无返回结果
     */
    private Integer status;

    /**
     * 状态码对应的信息
     */
    private String message;

    /**
     * 返回的结果
     */
    private Result result;

    @Data
    public static class Result {

        BaiduMapLocation origin;

        BaiduMapLocation destination;

        /**
         * 返回的方案集
         */
        private Route[] routes;

        @Data
        public static class Route {
            /**
             * 方案距离，单位：米
             */
            private Integer distance;

            /**
             * 方案预估时间，单位：秒
             */
            private Integer duration;

            /**
             * 路线的过路费预估，单位：元
             */
            private Integer toll;

            /**
             * 路线的整体路况评价
             * 取值范围
             * 0：无路况
             * 1：畅通
             * 2：缓行
             * 3：拥堵
             * 4：严重拥堵
             */
            @JSONField(name = "traffic_condition")
            private String trafficCondition;

            /**
             * 限行信息
             */
            @JSONField(name = "restriction_info")
            private RestrictionInfo restrictionInfo;

            /**
             * 路线分段
             */
            private Step[] steps;

            @Data
            public static class RestrictionInfo {
                /**
                 * 限行状态
                 * 0：不限行
                 * 1：已规避限行的路线合法
                 * 2：无法规避限行的非法路线
                 */
                private Integer status;
            }

            @Data
            public static class Step {
                /**
                 * 途径点序号
                 * 途径点序号为从0开始的整数，用于标识step所属的途径点路段
                 * 如：若该step属于起点至第一个途径中的路段，则其leg_index为0
                 */
                @JSONField(name = "leg_index")
                private Integer legIndex;

                /**
                 * 进入道路的角度
                 * 枚举值，返回值在0-11之间的一个值，共12个枚举值，以30度递进，即每个值代表角度范围为30度；
                 * 其中返回"0"代表345度到15度，以此类推，返回"11"代表315度到345度"；
                 * 分别代表的含义是：
                 * 0-[345°-15°]；1-[15°-45°]；2-[45°-75°]；3-[75°-105°]；4-[105°-135°]；5-[135°-165°]；
                 * 6-[165°-195°]；7-[195°-225°]；8-[225°-255°]；9-[255°-285°]；10-[285°-315°]；11-[315°-345°]
                 * 注：角度为与正北方向顺时针夹角
                 */
                private Integer direction;

                /**
                 * 机动转向点，包括基准八个方向、环岛、分歧等
                 * 枚举值，返回0-16之间的一个值，共17个枚举值。分别代表的含义是：
                 * 0：无效
                 * 1：直行
                 * 2：右前方转弯
                 * 3：右转
                 * 4：右后方转弯
                 * 5：掉头
                 * 6：左后方转弯
                 * 7：左转
                 * 8：左前方转弯
                 * 9：左侧
                 * 10：右侧
                 * 11：分歧-左
                 * 12：分歧中央
                 * 13：分歧右
                 * 14：环岛
                 * 15：进渡口
                 * 16：出渡口
                 */
                private Integer turn;

                /**
                 * 路段距离，单位：米
                 */
                private Integer distance;

                /**
                 * 路段耗时，单位：秒
                 */
                private Integer duration;

                /**
                 * 路段途经的道路类型列表
                 * 若途经多个路段类别，将用英文逗号","分隔，如：
                 * - 该路段依次途经国道和省道两种道路类型，则road_types:"2,3"
                 * - 该路段仅途经高速，则road_types:"0"
                 * road_types取值范围：
                 * 0：高速路
                 * 1：城市高速路
                 * 2：国道
                 * 3：省道
                 * 4：县道
                 * 5：乡镇村道
                 * 6：其他道路
                 * 7：九级路
                 * 8：航线(轮渡)
                 * 9：行人道路
                 */
                @JSONField(name = "road_types")
                private String roadTypes;

                /**
                 * 路段描述
                 */
                private String instruction;

                @JSONField(name = "start_location")
                private BaiduMapLocation startLocation;

                @JSONField(name = "end_location")
                private BaiduMapLocation endLocation;

                /**
                 * 分段坐标
                 */
                private String path;

                /**
                 * 分段路况详情
                 */
                private String trafficCondition;

                /**
                 * 路况指数
                 * 取值范围
                 * 0： 无路况
                 * 1： 畅通
                 * 2： 缓行
                 * 3： 拥堵
                 * 4： 严重拥堵
                 */
                private Integer status;

                /**
                 * 从当前坐标点开始，path中路况相同的坐标点个数
                 */
                @JSONField(name = "geo_cnt")
                private Integer geoCnt;
            }
        }
    }
}
