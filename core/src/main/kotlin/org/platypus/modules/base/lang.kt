package org.platypus.modules.base

/**
 * @author chmuchme
 * @since 0.1
 * on 09/09/17.
 */


//object ResLang : Model<ResLangEntity>() {
//
////    val jvmLocale = native("jvmLocale", Locale::class)
//    val name = string("name")
//    val code = string("code")
//    val iso_code = string("iso_code")
//    val translatable = boolean("translatable")
//    val active = boolean("active")
////    val direction = selection(["sdfknhd", "asdnk"], "Direction", required=True),
//
//    val date_format = string("date_format", "Date Format", required = true)
//    val time_format = string("time_format", "Time Format", required = true)
//    val grouping = string("grouping", "Separator Format", required = true,
//            help = """The Separator Format should be like [,n] where 0 < n :
//                |starting from Unit digit.-1 will end the separation. e.g. [3,2,-1] will represent 106500 to be 1,06,500;
//                |[1,2,-1] will represent it to be 106,50,0;
//                |[3] will represent it as 106,500.
//                |Provided ',' as the thousand separator in each case.""".trimMargin())
//    val decimal_point = string("decimal_point", "Decimal Separator", required = true)
//    val thousands_sep = string("thousands_sep", "Thousands Separator")
//}