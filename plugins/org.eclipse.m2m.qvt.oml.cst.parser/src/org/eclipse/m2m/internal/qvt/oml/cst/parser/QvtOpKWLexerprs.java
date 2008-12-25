/**
* <copyright>
*
* Copyright (c) 2005, 2007 IBM Corporation and others.
* All rights reserved.   This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   IBM - Initial API and implementation
*   E.D.Willink - Lexer and Parser refactoring to support extensibility and flexible error handling
*
* </copyright>
*
* $Id: QvtOpKWLexerprs.java,v 1.35 2008/12/25 19:24:04 sboyko Exp $
*/
/**
* <copyright>
*
* Copyright (c) 2006-2008 Borland Inc.
* All rights reserved.   This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   Borland - Initial API and implementation
*
* </copyright>
*
* $Id: QvtOpKWLexerprs.java,v 1.35 2008/12/25 19:24:04 sboyko Exp $
*/

package org.eclipse.m2m.internal.qvt.oml.cst.parser;

public class QvtOpKWLexerprs implements lpg.lpgjavaruntime.ParseTable, QvtOpKWLexersym {

    public interface IsKeyword {
        public final static byte isKeyword[] = {0,
            0,0,0,0,0,0,0,0,0,0,
            0,0,0,0,0,0,0,0,0,0,
            0,0,0,0,0,0,0,0,0,0,
            0,0,0,0,0,0,0,0,0,0,
            0,0,0,0,0,0,0,0,0,0,
            0,0,0,0
        };
    };
    public final static byte isKeyword[] = IsKeyword.isKeyword;
    public final boolean isKeyword(int index) { return isKeyword[index] != 0; }

    public interface BaseCheck {
        public final static byte baseCheck[] = {0,
            4,4,2,4,4,5,3,2,3,3,
            7,3,2,4,5,3,3,8,10,10,
            7,6,6,8,3,3,7,6,6,13,
            8,7,11,11,9,8,14,12,12,12,
            6,7,16,4,7,5,6,7,7,10,
            4,10,1,3,5,3,6,14,6,7,
            9,9,6,8,6,6,7,5,6,5,
            4,3,13,10,12,8,9,5,3,4,
            3,4,3,6,4,7,10,9,12,10,
            13,12,15,9,4,5,7,6,7,8,
            8,6,6,4,4,6,4,7,8,9,
            10,13,16,7,6,7,4,4,5,8,
            11,8,8,7,7,2,4,4,6,9,
            4,7,7,9,5,8,10,3,3,7,
            9,7
        };
    };
    public final static byte baseCheck[] = BaseCheck.baseCheck;
    public final int baseCheck(int index) { return baseCheck[index]; }
    public final static byte rhs[] = baseCheck;
    public final int rhs(int index) { return rhs[index]; };

    public interface BaseAction {
        public final static char baseAction[] = {
            1,2,2,2,2,2,2,2,2,2,
            2,2,2,2,2,2,2,2,2,2,
            2,2,2,2,2,2,2,2,2,2,
            2,2,2,2,2,2,2,2,2,2,
            2,2,2,2,2,2,2,2,2,2,
            2,2,2,1,1,1,1,1,1,1,
            1,1,1,1,1,1,1,1,1,1,
            1,1,1,1,1,1,1,1,1,1,
            1,1,1,1,1,1,1,1,1,1,
            1,1,1,1,1,1,1,1,1,1,
            1,1,1,1,1,1,1,1,1,1,
            1,1,1,1,1,1,1,1,1,1,
            1,1,1,1,1,1,1,1,1,1,
            1,1,1,1,1,1,1,1,1,1,
            1,1,1,1,146,53,55,175,257,120,
            171,64,118,105,248,84,115,166,258,266,
            259,268,177,127,146,269,180,191,130,95,
            140,33,37,57,142,103,66,112,272,273,
            274,279,276,280,194,284,285,288,289,188,
            296,287,132,195,204,299,181,291,295,300,
            205,303,51,305,209,144,308,309,311,312,
            313,315,211,316,318,322,319,327,215,331,
            324,332,335,334,339,341,342,344,345,349,
            346,353,355,356,358,359,365,210,151,17,
            82,360,362,157,224,77,374,363,74,377,
            378,372,382,384,387,383,391,392,393,394,
            395,399,397,404,402,400,413,403,414,367,
            405,416,417,419,219,422,423,425,426,434,
            435,432,438,229,231,440,442,443,445,448,
            450,451,158,452,90,456,459,460,463,461,
            462,25,469,470,475,472,478,479,481,482,
            483,488,220,490,494,497,499,486,501,503,
            506,493,509,507,514,516,519,523,520,526,
            527,510,232,242,531,528,533,535,536,539,
            542,200,165,543,545,546,547,549,551,235,
            553,554,556,557,559,563,561,564,572,576,
            580,568,578,570,585,582,588,589,593,594,
            590,595,597,601,598,244,604,609,612,613,
            615,619,617,621,622,627,631,633,634,637,
            638,623,641,639,643,646,648,651,644,653,
            655,657,659,661,662,668,663,669,673,664,
            674,677,679,680,681,684,686,690,693,694,
            695,697,698,699,704,705,706,709,708,712,
            713,717,720,715,722,728,729,733,734,735,
            736,723,741,745,747,749,737,752,754,756,
            742,759,760,763,761,765,767,768,770,772,
            771,773,778,783,779,781,791,785,793,794,
            789,802,798,800,805,780,70,811,806,809,
            815,816,818,819,822,823,825,826,829,832,
            835,833,838,840,841,828,842,846,847,844,
            854,858,859,863,860,864,867,868,869,870,
            872,873,877,874,884,876,881,891,893,895,
            897,889,899,900,902,903,908,905,906,907,
            915,914,919,920,921,922,923,927,928,930,
            931,935,938,941,940,942,946,943,950,949,
            953,956,957,959,960,961,965,967,972,973,
            18,974,977,976,978,979,981,984,985,987,
            990,989,245,991,1000,993,1002,162,998,1007,
            1008,1009,1011,1015,1018,1016,234,1020,602,1021,
            1022,1024,1023,1027,1032,1038,1035,1033,1042,1043,
            1045,1046,1049,1051,1053,1054,1056,1052,1064,1066,
            1067,1071,1061,1074,1076,1077,1080,1072,1081,1083,
            1085,1086,1091,1088,1096,1098,1094,1100,1101,1102,
            1106,1107,1108,1110,1112,1114,1115,1118,1120,1123,
            1125,1128,1121,1130,1134,1135,1137,1139,1141,1142,
            1143,1146,1147,1148,1150,1151,1152,1153,1162,1159,
            1165,1168,1169,1172,1174,1175,1177,1179,246,1180,
            1183,1184,1186,1190,1191,1193,1196,1198,1197,1199,
            1205,1206,1210,1204,1212,1217,1213,1220,1214,1225,
            1222,1218,1227,1228,1229,1233,1232,1234,1236,1243,
            702,702
        };
    };
    public final static char baseAction[] = BaseAction.baseAction;
    public final int baseAction(int index) { return baseAction[index]; }
    public final static char lhs[] = baseAction;
    public final int lhs(int index) { return lhs[index]; };

    public interface TermCheck {
        public final static byte termCheck[] = {0,
            0,1,2,3,4,5,6,7,8,9,
            10,11,12,13,14,15,0,0,18,19,
            20,21,22,23,0,25,26,11,12,29,
            30,31,0,33,34,35,0,5,38,3,
            40,9,10,26,8,21,10,15,12,13,
            0,27,2,3,0,5,0,7,22,3,
            36,5,6,0,14,0,10,2,3,0,
            5,8,9,0,24,2,0,4,22,14,
            15,0,9,0,1,4,5,4,19,0,
            7,18,9,20,0,1,42,28,4,30,
            31,7,0,9,0,1,37,31,19,7,
            8,0,1,2,0,1,27,0,16,0,
            9,7,20,9,7,6,0,8,39,0,
            41,0,6,7,23,9,7,8,9,0,
            1,0,11,0,3,0,3,6,9,6,
            0,12,2,8,4,10,0,0,15,2,
            10,0,5,7,0,0,5,26,12,4,
            0,14,16,3,0,5,0,1,4,0,
            0,1,21,7,4,20,7,0,9,28,
            0,1,2,0,0,8,32,4,4,0,
            1,14,9,0,0,2,2,13,0,0,
            0,8,2,14,0,7,2,9,0,0,
            11,3,18,0,1,16,8,4,0,1,
            0,0,4,0,0,25,22,6,19,9,
            9,0,12,0,0,0,27,0,7,8,
            16,18,9,9,9,12,0,0,0,12,
            4,3,29,19,19,0,1,0,0,12,
            3,0,0,0,1,0,5,9,0,0,
            1,6,10,0,0,7,0,0,0,1,
            0,5,8,6,0,0,13,2,0,0,
            2,7,0,13,0,6,4,0,0,5,
            0,0,0,6,0,0,6,0,0,11,
            9,0,8,0,9,8,0,6,2,17,
            0,0,2,0,0,17,2,6,0,1,
            0,0,9,0,0,0,23,7,0,8,
            2,6,0,1,0,0,13,0,0,0,
            5,0,0,5,0,11,0,10,24,3,
            6,0,13,0,1,13,0,0,17,8,
            4,0,0,0,7,2,0,6,2,7,
            0,0,0,0,0,4,0,5,0,0,
            10,0,0,0,0,4,7,13,15,13,
            12,8,0,0,1,0,0,13,0,7,
            0,0,0,7,0,0,24,6,6,4,
            6,0,17,0,0,1,5,0,20,0,
            7,0,0,1,0,1,7,0,11,0,
            0,0,5,12,5,0,1,6,0,0,
            0,0,0,13,6,6,4,0,0,0,
            1,0,12,5,0,1,15,0,0,8,
            0,0,0,6,6,0,6,0,1,0,
            1,9,0,0,13,2,0,1,0,1,
            0,1,0,1,19,0,0,15,0,0,
            5,0,4,0,1,0,1,8,0,0,
            14,3,0,1,5,0,0,0,3,3,
            0,1,0,1,0,0,2,2,0,12,
            2,0,0,2,0,0,0,1,0,4,
            0,1,0,0,6,0,0,2,0,15,
            0,1,0,0,6,12,4,0,12,0,
            18,0,1,0,32,0,1,0,15,0,
            1,0,15,6,0,1,17,0,0,0,
            9,3,0,0,0,8,0,0,6,6,
            0,0,8,0,15,5,10,4,0,12,
            2,0,0,1,0,4,0,3,0,1,
            0,0,0,3,23,9,0,1,6,8,
            0,1,0,0,1,3,0,0,0,3,
            0,1,0,0,7,0,8,0,1,7,
            0,6,0,10,0,5,0,5,0,5,
            0,0,0,0,8,5,8,0,0,8,
            3,3,0,0,2,13,0,14,0,0,
            0,5,4,0,11,0,7,4,8,0,
            1,6,0,0,0,2,0,0,0,1,
            8,4,8,0,0,0,10,0,0,4,
            2,0,0,10,0,8,0,1,7,0,
            8,0,0,1,3,6,22,0,0,2,
            16,3,0,0,0,0,0,5,4,4,
            0,0,9,3,0,1,0,1,0,8,
            2,0,1,0,18,0,3,2,0,0,
            0,2,0,5,0,3,0,0,8,0,
            0,0,0,1,5,4,10,0,0,0,
            0,17,0,3,0,18,16,10,0,7,
            0,1,0,0,10,17,8,0,6,0,
            7,0,1,4,0,0,2,10,0,30,
            0,1,7,5,0,0,2,0,0,2,
            5,0,0,1,0,0,5,0,0,11,
            2,0,0,2,0,1,4,0,14,0,
            0,0,5,0,4,0,0,20,2,0,
            25,10,9,0,15,2,11,0,0,0,
            2,4,0,0,2,2,0,0,0,0,
            11,0,0,0,2,0,0,10,3,13,
            0,8,11,0,16,5,17,11,0,6,
            0,1,0,1,0,1,0,1,0,0,
            12,0,0,2,0,0,0,0,2,7,
            12,12,5,0,0,11,11,3,0,0,
            0,0,0,10,2,6,0,0,2,0,
            0,10,3,3,0,1,16,0,20,0,
            0,0,0,16,4,0,4,2,0,0,
            11,10,0,16,5,0,0,1,0,0,
            0,9,14,5,0,1,0,12,2,9,
            11,0,0,0,1,0,0,0,0,1,
            0,4,2,0,0,1,0,1,0,0,
            0,16,0,21,18,5,13,0,10,0,
            1,0,10,6,33,16,0,0,0,8,
            0,1,4,7,0,0,2,0,1,0,
            0,0,0,0,3,2,0,0,13,7,
            11,0,0,13,0,9,29,0,1,8,
            6,0,0,11,0,0,5,3,0,1,
            0,0,0,0,1,0,11,5,16,9,
            0,6,11,0,1,0,0,7,3,3,
            0,0,2,0,1,0,0,1,3,0,
            0,1,0,4,0,0,1,0,17,5,
            0,9,2,0,7,0,1,0,1,0,
            0,0,3,3,11,0,0,0,1,0,
            4,0,3,0,0,1,15,0,5,0,
            0,2,0,18,0,1,9,0,6,0,
            1,11,5,0,0,2,0,3,0,28,
            0,0,0,7,6,0,0,0,1,0,
            0,0,0,7,14,10,7,7,0,1,
            8,0,21,21,0,4,2,0,0,2,
            19,0,4,0,0,4,0,1,0,0,
            2,2,0,0,2,0,3,14,14,0,
            0,1,0,1,9,0,0,0,0,10,
            3,5,4,0,0,0,11,3,3,0,
            1,0,0,0,3,12,0,0,2,0,
            1,0,9,11,0,1,0,0,0,8,
            3,0,0,0,3,0,3,11,21,7,
            0,6,0,1,0,0,0,19,0,0,
            0,0,0,0,0,0,0,0,0,0,
            0,0,0,0,0,0,0,0,0,0,
            0,0,0,0,0,0,0,0,0,0,
            0,0,0,0,0,0,0,0,0,0,
            0,0,0,0,0,0,0
        };
    };
    public final static byte termCheck[] = TermCheck.termCheck;
    public final int termCheck(int index) { return termCheck[index]; }

    public interface TermAction {
        public final static char termAction[] = {0,
            702,174,175,170,176,177,169,173,162,172,
            163,155,150,151,168,156,702,702,152,161,
            153,164,149,157,702,154,171,322,321,167,
            158,160,702,166,165,148,702,224,147,231,
            159,226,223,802,232,389,230,225,229,227,
            702,391,286,285,702,284,702,282,228,237,
            390,235,236,702,283,702,234,246,248,702,
            245,184,185,13,287,334,702,337,233,705,
            247,702,335,702,189,323,324,191,539,702,
            190,333,828,336,702,219,701,541,218,542,
            540,217,702,216,702,187,543,330,380,242,
            243,702,252,250,702,194,383,702,241,702,
            251,193,244,192,186,181,702,180,381,702,
            382,702,204,203,249,205,215,213,214,702,
            220,702,268,702,239,702,292,240,222,293,
            702,221,319,207,320,206,702,702,291,376,
            318,27,377,326,702,702,613,238,327,195,
            702,378,841,182,702,183,702,202,178,702,
            702,274,614,201,275,196,210,702,209,615,
            702,212,211,702,702,264,821,259,269,702,
            424,265,260,702,702,272,280,270,702,702,
            702,271,718,703,702,289,304,290,702,702,
            709,773,279,702,329,727,358,328,702,366,
            702,702,367,702,702,299,305,415,400,369,
            414,702,368,702,86,90,399,702,416,417,
            770,623,451,610,681,452,702,702,702,188,
            179,199,622,609,680,702,198,702,702,197,
            200,702,702,702,255,702,253,208,702,702,
            258,257,254,702,702,256,702,702,702,263,
            702,267,774,262,702,702,261,266,702,702,
            273,277,702,276,702,278,281,702,702,288,
            702,702,702,294,702,702,296,702,702,295,
            297,702,298,702,300,301,702,302,303,719,
            702,702,714,702,702,785,712,306,702,308,
            702,702,307,702,702,702,783,309,702,711,
            758,312,702,728,8,702,310,702,702,702,
            314,702,702,316,702,313,702,315,311,806,
            317,702,325,702,331,332,702,702,840,340,
            338,702,702,702,339,820,702,341,819,342,
            702,702,702,702,702,344,702,797,702,702,
            343,702,702,702,79,350,351,346,345,348,
            347,349,702,702,354,702,702,355,702,352,
            702,702,702,357,702,702,353,359,360,361,
            746,702,356,702,702,363,364,702,787,702,
            362,702,702,372,702,809,370,702,365,702,
            702,702,373,371,374,702,384,379,702,702,
            702,702,702,375,385,386,387,702,702,702,
            784,702,388,392,702,393,833,702,702,394,
            702,702,702,753,395,702,396,702,398,702,
            401,397,702,702,782,402,702,403,702,404,
            702,406,702,407,405,702,702,830,54,702,
            408,702,409,702,707,702,410,413,702,702,
            829,411,702,716,807,702,702,702,706,412,
            702,418,702,420,702,702,704,421,702,419,
            422,702,702,423,702,702,702,428,702,427,
            702,430,702,702,429,702,702,433,702,426,
            702,437,702,702,435,432,436,702,434,702,
            431,702,798,702,425,702,757,702,438,702,
            748,702,439,440,702,837,441,702,702,702,
            442,444,702,702,702,443,702,702,445,446,
            702,702,448,702,447,780,449,453,702,450,
            454,702,702,456,702,455,702,457,702,458,
            702,702,702,460,738,459,702,462,468,461,
            702,463,702,702,465,464,702,702,702,466,
            702,717,702,702,467,702,469,702,472,470,
            702,471,702,474,702,473,702,475,702,476,
            702,702,702,702,477,479,478,702,702,480,
            481,483,702,702,484,482,702,708,702,702,
            702,486,487,702,485,702,488,490,489,702,
            492,491,702,702,702,772,702,702,702,497,
            493,496,494,702,702,702,495,702,702,500,
            501,702,702,498,702,502,702,504,503,702,
            771,702,702,513,507,505,499,702,702,508,
            506,509,702,702,702,702,702,765,511,512,
            702,702,510,514,702,515,702,516,702,518,
            805,702,768,702,517,702,767,731,702,702,
            702,520,702,519,702,521,702,702,522,702,
            702,702,702,528,526,527,524,702,702,702,
            702,523,702,531,702,525,749,529,702,530,
            702,817,702,702,532,743,534,702,724,702,
            533,702,535,537,702,702,759,536,702,538,
            702,544,545,800,702,702,786,702,702,547,
            546,702,702,549,702,702,725,702,702,548,
            552,702,702,761,702,553,554,702,550,702,
            702,702,555,702,557,702,702,808,559,702,
            551,804,560,702,556,561,558,702,702,702,
            563,562,702,702,564,826,702,702,702,702,
            827,702,702,702,569,702,702,566,571,565,
            702,744,568,702,567,799,769,751,702,570,
            702,572,702,734,702,573,702,818,702,702,
            574,702,702,577,702,702,702,702,581,578,
            575,576,579,702,702,750,580,747,702,702,
            702,702,702,582,810,834,702,702,584,702,
            702,583,586,587,702,588,762,702,816,702,
            702,702,129,585,590,702,593,592,702,702,
            835,591,702,589,801,702,702,723,702,702,
            702,594,842,713,702,597,702,595,598,596,
            844,702,28,702,601,702,702,702,702,825,
            702,603,604,702,702,606,702,607,702,702,
            702,778,702,600,602,822,605,702,608,702,
            824,702,611,616,599,838,702,702,702,612,
            702,720,619,617,702,702,811,702,620,702,
            702,702,702,702,626,803,702,702,621,627,
            624,702,702,625,702,628,618,702,726,629,
            630,702,702,631,702,702,766,632,702,836,
            702,702,702,702,796,702,843,764,733,633,
            702,763,634,702,635,702,702,638,790,636,
            702,702,637,702,639,702,702,641,640,702,
            702,644,702,642,702,702,737,702,643,779,
            702,645,646,702,647,702,648,702,649,702,
            702,702,651,832,650,702,702,702,812,702,
            654,702,776,702,702,656,652,702,839,702,
            702,658,702,653,702,813,657,702,659,702,
            752,754,660,702,702,722,702,721,702,655,
            702,702,702,661,662,702,702,702,668,702,
            702,87,702,667,663,666,669,670,702,673,
            823,702,664,665,702,672,674,702,702,675,
            671,702,676,702,702,677,702,678,702,702,
            679,682,702,702,683,702,791,736,735,702,
            702,686,702,741,684,702,702,702,702,685,
            687,742,688,702,702,702,740,794,689,702,
            777,702,702,702,775,690,702,112,691,702,
            692,702,693,732,702,694,702,702,91,695,
            760,702,702,702,699,702,795,739,696,698,
            702,745,702,815,702,702,702,697
        };
    };
    public final static char termAction[] = TermAction.termAction;
    public final int termAction(int index) { return termAction[index]; }
    public final int asb(int index) { return 0; }
    public final int asr(int index) { return 0; }
    public final int nasb(int index) { return 0; }
    public final int nasr(int index) { return 0; }
    public final int terminalIndex(int index) { return 0; }
    public final int nonterminalIndex(int index) { return 0; }
    public final int scopePrefix(int index) { return 0;}
    public final int scopeSuffix(int index) { return 0;}
    public final int scopeLhs(int index) { return 0;}
    public final int scopeLa(int index) { return 0;}
    public final int scopeStateSet(int index) { return 0;}
    public final int scopeRhs(int index) { return 0;}
    public final int scopeState(int index) { return 0;}
    public final int inSymb(int index) { return 0;}
    public final String name(int index) { return null; }
    public final int getErrorSymbol() { return 0; }
    public final int getScopeUbound() { return 0; }
    public final int getScopeSize() { return 0; }
    public final int getMaxNameLength() { return 0; }

    public final static int
           NUM_STATES        = 555,
           NT_OFFSET         = 54,
           LA_STATE_OFFSET   = 844,
           MAX_LA            = 1,
           NUM_RULES         = 142,
           NUM_NONTERMINALS  = 3,
           NUM_SYMBOLS       = 57,
           SEGMENT_SIZE      = 8192,
           START_STATE       = 143,
           IDENTIFIER_SYMBOL = 0,
           EOFT_SYMBOL       = 42,
           EOLT_SYMBOL       = 55,
           ACCEPT_ACTION     = 701,
           ERROR_ACTION      = 702;

    public final static boolean BACKTRACK = false;

    public final int getNumStates() { return NUM_STATES; }
    public final int getNtOffset() { return NT_OFFSET; }
    public final int getLaStateOffset() { return LA_STATE_OFFSET; }
    public final int getMaxLa() { return MAX_LA; }
    public final int getNumRules() { return NUM_RULES; }
    public final int getNumNonterminals() { return NUM_NONTERMINALS; }
    public final int getNumSymbols() { return NUM_SYMBOLS; }
    public final int getSegmentSize() { return SEGMENT_SIZE; }
    public final int getStartState() { return START_STATE; }
    public final int getStartSymbol() { return lhs[0]; }
    public final int getIdentifierSymbol() { return IDENTIFIER_SYMBOL; }
    public final int getEoftSymbol() { return EOFT_SYMBOL; }
    public final int getEoltSymbol() { return EOLT_SYMBOL; }
    public final int getAcceptAction() { return ACCEPT_ACTION; }
    public final int getErrorAction() { return ERROR_ACTION; }
    public final boolean isValidForParser() { return isValidForParser; }
    public final boolean getBacktrack() { return BACKTRACK; }

    public final int originalState(int state) { return 0; }
    public final int asi(int state) { return 0; }
    public final int nasi(int state) { return 0; }
    public final int inSymbol(int state) { return 0; }

    public final int ntAction(int state, int sym) {
        return baseAction[state + sym];
    }

    public final int tAction(int state, int sym) {
        int i = baseAction[state],
            k = i + sym;
        return termAction[termCheck[k] == sym ? k : i];
    }
    public final int lookAhead(int la_state, int sym) {
        int k = la_state + sym;
        return termAction[termCheck[k] == sym ? k : la_state];
    }
}
