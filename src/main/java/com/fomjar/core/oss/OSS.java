package com.fomjar.core.oss;

import org.springframework.web.multipart.MultipartFile;

import java.io.*;

/**
 * OSS的抽象定义。
 *
 * @author fomjar
 */
public abstract class OSS {

    private String bucket;

    /**
     * 获取存储篮。
     *
     * @return 名称
     */
    public String bucket() {return this.bucket;}

    /**
     * 设置存储篮。
     *
     * @param bucket 名称
     * @return 此OSS对象
     */
    public OSS bucket(String bucket) {
        this.bucket = bucket;
        return this;
    }

    /**
     * 上传文件。
     *
     * @param file 待上传的文件
     * @return 文件访问地址
     * @throws IOException 上传失败
     */
    public String upload(MultipartFile file) throws IOException {
        return this.upload(file.getOriginalFilename(), file);
    }

    /**
     * 上传文件。
     *
     * @param name 文件名
     * @param file 待上传的文件
     * @return 文件访问地址
     * @throws IOException 上传失败
     */
    public String upload(String name, MultipartFile file) throws IOException {
        InputStream is = file.getInputStream();
        try     {return upload(name, is);}
        finally {is.close();}
    }


    /**
     * 上传文件。
     *
     * @param file 待上传的文件
     * @return 文件访问地址
     * @throws IOException 上传失败
     */
    public String upload(File file) throws IOException {
        return this.upload(file.getName(), file);
    }


    /**
     * 上传文件。
     *
     * @param name 文件名
     * @param file 待上传的文件
     * @return 文件访问地址
     * @throws IOException 上传失败
     */
    public String upload(String name, File file) throws IOException {
        InputStream is = new FileInputStream(file);
        try     {return upload(name, is);}
        finally {is.close();}
    }

    /**
     * 上传文件。
     *
     * @param name 文件名
     * @param bytes 文件内容
     * @return 文件访问地址
     * @throws IOException 上传失败
     */
    public String upload(String name, byte[] bytes) throws IOException {
        return upload(name, new ByteArrayInputStream(bytes));
    }

    /**
     * 上传文件。
     *
     * @param name 文件名
     * @param is 文件内容
     * @return 文件访问地址
     * @throws IOException 上传失败
     */
    public abstract String upload(String name, InputStream is) throws IOException;

    /**
     * 关闭OSS客户端。
     */
    public abstract void shutdown();

    /**
     * 根据文件名推断其Content-Type属性。
     *
     * @param file 文件名
     * @return mime值
     */
    public static String filename2contenttype(String file) {
        String defaultType = "application/octet-stream";

        if (!file.contains(".")) return defaultType;

        switch (file.substring(file.lastIndexOf(".")).toLowerCase()) {
            case ".*":      return "application/octet-stream";
            case ".001":    return "application/x-001";
            case ".301":    return "application/x-301";
            case ".323":    return "text/h323";
            case ".906":    return "application/x-906";
            case ".907":    return "drawing/907";
            case ".a11":    return "application/x-a11";
            case ".acp":    return "audio/x-mei-aac";
            case ".ai":     return "application/postscript";
            case ".aif":    return "audio/aiff";
            case ".aifc":   return "audio/aiff";
            case ".aiff":   return "audio/aiff";
            case ".anv":    return "application/x-anv";
            case ".asa":    return "text/asa";
            case ".asf":    return "video/x-ms-asf";
            case ".asp":    return "text/asp";
            case ".asx":    return "video/x-ms-asf";
            case ".au":     return "audio/basic";
            case ".avi":    return "video/avi";
            case ".awf":    return "application/vnd.adobe.workflow";
            case ".biz":    return "text/xml";
            case ".bmp":    return "application/x-bmp";
            case ".bot":    return "application/x-bot";
            case ".c4t":    return "application/x-c4t";
            case ".c90":    return "application/x-c90";
            case ".cal":    return "application/x-cals";
            case ".cat":    return "application/vnd.ms-pki.seccat";
            case ".cdf":    return "application/x-netcdf";
            case ".cdr":    return "application/x-cdr";
            case ".cel":    return "application/x-cel";
            case ".cer":    return "application/x-x509-ca-cert";
            case ".cg4":    return "application/x-g4";
            case ".cgm":    return "application/x-cgm";
            case ".cit":    return "application/x-cit";
            case ".class":  return "java/*";
            case ".cml":    return "text/xml";
            case ".cmp":    return "application/x-cmp";
            case ".cmx":    return "application/x-cmx";
            case ".cot":    return "application/x-cot";
            case ".crl":    return "application/pkix-crl";
            case ".crt":    return "application/x-x509-ca-cert";
            case ".csi":    return "application/x-csi";
            case ".css":    return "text/css";
            case ".cut":    return "application/x-cut";
            case ".dbf":    return "application/x-dbf";
            case ".dbm":    return "application/x-dbm";
            case ".dbx":    return "application/x-dbx";
            case ".dcd":    return "text/xml";
            case ".dcx":    return "application/x-dcx";
            case ".der":    return "application/x-x509-ca-cert";
            case ".dgn":    return "application/x-dgn";
            case ".dib":    return "application/x-dib";
            case ".dll":    return "application/x-msdownload";
            case ".doc":    return "application/msword";
            case ".dot":    return "application/msword";
            case ".drw":    return "application/x-drw";
            case ".dtd":    return "text/xml";
            case ".dwf":    return "application/x-dwf";
            case ".dwg":    return "application/x-dwg";
            case ".dxb":    return "application/x-dxb";
            case ".dxf":    return "application/x-dxf";
            case ".edn":    return "application/vnd.adobe.edn";
            case ".emf":    return "application/x-emf";
            case ".eml":    return "message/rfc822";
            case ".ent":    return "text/xml";
            case ".epi":    return "application/x-epi";
            case ".eps":    return "application/x-ps";
            case ".etd":    return "application/x-ebx";
            case ".exe":    return "application/x-msdownload";
            case ".fax":    return "image/fax";
            case ".fdf":    return "application/vnd.fdf";
            case ".fif":    return "application/fractals";
            case ".fo":     return "text/xml";
            case ".frm":    return "application/x-frm";
            case ".g4":     return "application/x-g4";
            case ".gbr":    return "application/x-gbr";
            case ".":       return "application/x-";
            case ".gif":    return "image/gif";
            case ".gl2":    return "application/x-gl2";
            case ".gp4":    return "application/x-gp4";
            case ".hgl":    return "application/x-hgl";
            case ".hmr":    return "application/x-hmr";
            case ".hpg":    return "application/x-hpgl";
            case ".hpl":    return "application/x-hpl";
            case ".hqx":    return "application/mac-binhex40";
            case ".hrf":    return "application/x-hrf";
            case ".hta":    return "application/hta";
            case ".htc":    return "text/x-component";
            case ".htm":    return "text/html";
            case ".html":   return "text/html";
            case ".htt":    return "text/webviewhtml";
            case ".htx":    return "text/html";
            case ".icb":    return "application/x-icb";
            case ".ico":    return "image/x-icon";
            case ".iff":    return "application/x-iff";
            case ".ig4":    return "application/x-g4";
            case ".igs":    return "application/x-igs";
            case ".iii":    return "application/x-iphone";
            case ".img":    return "application/x-img";
            case ".ins":    return "application/x-internet-signup";
            case ".isp":    return "application/x-internet-signup";
            case ".IVF":    return "video/x-ivf";
            case ".java":   return "java/*";
            case ".jfif":   return "image/jpeg";
            case ".jpe":    return "image/jpeg";
            case ".jpeg":   return "image/jpeg";
            case ".jpg":    return "image/jpeg";
            case ".js":     return "application/x-javascript";
            case ".jsp":    return "text/html";
            case ".la1":    return "audio/x-liquid-file";
            case ".lar":    return "application/x-laplayer-reg";
            case ".latex":  return "application/x-latex";
            case ".lavs":   return "audio/x-liquid-secure";
            case ".lbm":    return "application/x-lbm";
            case ".lmsff":  return "audio/x-la-lms";
            case ".ls":     return "application/x-javascript";
            case ".ltr":    return "application/x-ltr";
            case ".m1v":    return "video/x-mpeg";
            case ".m2v":    return "video/x-mpeg";
            case ".m3u":    return "audio/mpegurl";
            case ".m4e":    return "video/mpeg4";
            case ".mac":    return "application/x-mac";
            case ".man":    return "application/x-troff-man";
            case ".math":   return "text/xml";
            case ".mdb":    return "application/x-mdb";
            case ".mfp":    return "application/x-shockwave-flash";
            case ".mht":    return "message/rfc822";
            case ".mhtml":  return "message/rfc822";
            case ".mi":     return "application/x-mi";
            case ".mid":    return "audio/mid";
            case ".midi":   return "audio/mid";
            case ".mil":    return "application/x-mil";
            case ".mml":    return "text/xml";
            case ".mnd":    return "audio/x-musicnet-download";
            case ".mns":    return "audio/x-musicnet-stream";
            case ".mocha":  return "application/x-javascript";
            case ".movie":  return "video/x-sgi-movie";
            case ".mp1":    return "audio/mp1";
            case ".mp2":    return "audio/mp2";
            case ".mp2v":   return "video/mpeg";
            case ".mp3":    return "audio/mp3";
            case ".mp4":    return "video/mpeg4";
            case ".mpa":    return "video/x-mpg";
            case ".mpd":    return "application/vnd.ms-project";
            case ".mpe":    return "video/x-mpeg";
            case ".mpeg":   return "video/mpg";
            case ".mpg":    return "video/mpg";
            case ".mpga":   return "audio/rn-mpeg";
            case ".mpp":    return "application/vnd.ms-project";
            case ".mps":    return "video/x-mpeg";
            case ".mpt":    return "application/vnd.ms-project";
            case ".mpv":    return "video/mpg";
            case ".mpv2":   return "video/mpeg";
            case ".mpw":    return "application/vnd.ms-project";
            case ".mpx":    return "application/vnd.ms-project";
            case ".mtx":    return "text/xml";
            case ".mxp":    return "application/x-mmxp";
            case ".net":    return "image/pnetvue";
            case ".nrf":    return "application/x-nrf";
            case ".nws":    return "message/rfc822";
            case ".odc":    return "text/x-ms-odc";
            case ".out":    return "application/x-out";
            case ".p10":    return "application/pkcs10";
            case ".p12":    return "application/x-pkcs12";
            case ".p7b":    return "application/x-pkcs7-certificates";
            case ".p7c":    return "application/pkcs7-mime";
            case ".p7m":    return "application/pkcs7-mime";
            case ".p7r":    return "application/x-pkcs7-certreqresp";
            case ".p7s":    return "application/pkcs7-signature";
            case ".pc5":    return "application/x-pc5";
            case ".pci":    return "application/x-pci";
            case ".pcl":    return "application/x-pcl";
            case ".pcx":    return "application/x-pcx";
            case ".pdf":    return "application/pdf";
            case ".pdx":    return "application/vnd.adobe.pdx";
            case ".pfx":    return "application/x-pkcs12";
            case ".pgl":    return "application/x-pgl";
            case ".pic":    return "application/x-pic";
            case ".pko":    return "application/vnd.ms-pki.pko";
            case ".pl":     return "application/x-perl";
            case ".plg":    return "text/html";
            case ".pls":    return "audio/scpls";
            case ".plt":    return "application/x-plt";
            case ".png":    return "image/png";
            case ".pot":    return "application/vnd.ms-powerpoint";
            case ".ppa":    return "application/vnd.ms-powerpoint";
            case ".ppm":    return "application/x-ppm";
            case ".pps":    return "application/vnd.ms-powerpoint";
            case ".ppt":    return "application/x-ppt";
            case ".pr":     return "application/x-pr";
            case ".prf":    return "application/pics-rules";
            case ".prn":    return "application/x-prn";
            case ".prt":    return "application/x-prt";
            case ".ps":     return "application/x-ps";
            case ".ptn":    return "application/x-ptn";
            case ".pwz":    return "application/vnd.ms-powerpoint";
            case ".r3t":    return "text/vnd.rn-realtext3d";
            case ".ra":     return "audio/vnd.rn-realaudio";
            case ".ram":    return "audio/x-pn-realaudio";
            case ".ras":    return "application/x-ras";
            case ".rat":    return "application/rat-file";
            case ".rdf":    return "text/xml";
            case ".rec":    return "application/vnd.rn-recording";
            case ".red":    return "application/x-red";
            case ".rgb":    return "application/x-rgb";
            case ".rjs":    return "application/vnd.rn-realsystem-rjs";
            case ".rjt":    return "application/vnd.rn-realsystem-rjt";
            case ".rlc":    return "application/x-rlc";
            case ".rle":    return "application/x-rle";
            case ".rm":     return "application/vnd.rn-realmedia";
            case ".rmf":    return "application/vnd.adobe.rmf";
            case ".rmi":    return "audio/mid";
            case ".rmj":    return "application/vnd.rn-realsystem-rmj";
            case ".rmm":    return "audio/x-pn-realaudio";
            case ".rmp":    return "application/vnd.rn-rn_music_package";
            case ".rms":    return "application/vnd.rn-realmedia-secure";
            case ".rmvb":   return "application/vnd.rn-realmedia-vbr";
            case ".rmx":    return "application/vnd.rn-realsystem-rmx";
            case ".rnx":    return "application/vnd.rn-realplayer";
            case ".rp":     return "image/vnd.rn-realpix";
            case ".rpm":    return "audio/x-pn-realaudio-plugin";
            case ".rsml":   return "application/vnd.rn-rsml";
            case ".rt":     return "text/vnd.rn-realtext";
            case ".rtf":    return "application/x-rtf";
            case ".rv":     return "video/vnd.rn-realvideo";
            case ".sam":    return "application/x-sam";
            case ".sat":    return "application/x-sat";
            case ".sdp":    return "application/sdp";
            case ".sdw":    return "application/x-sdw";
            case ".sit":    return "application/x-stuffit";
            case ".slb":    return "application/x-slb";
            case ".sld":    return "application/x-sld";
            case ".slk":    return "drawing/x-slk";
            case ".smi":    return "application/smil";
            case ".smil":   return "application/smil";
            case ".smk":    return "application/x-smk";
            case ".snd":    return "audio/basic";
            case ".sol":    return "text/plain";
            case ".sor":    return "text/plain";
            case ".spc":    return "application/x-pkcs7-certificates";
            case ".spl":    return "application/futuresplash";
            case ".spp":    return "text/xml";
            case ".ssm":    return "application/streamingmedia";
            case ".sst":    return "application/vnd.ms-pki.certstore";
            case ".stl":    return "application/vnd.ms-pki.stl";
            case ".stm":    return "text/html";
            case ".sty":    return "application/x-sty";
            case ".svg":    return "text/xml";
            case ".swf":    return "application/x-shockwave-flash";
            case ".tdf":    return "application/x-tdf";
            case ".tg4":    return "application/x-tg4";
            case ".tga":    return "application/x-tga";
            case ".tif":    return "image/tiff";
            case ".tiff":   return "image/tiff";
            case ".tld":    return "text/xml";
            case ".top":    return "drawing/x-top";
            case ".torrent":return "application/x-bittorrent";
            case ".tsd":    return "text/xml";
            case ".txt":    return "text/plain";
            case ".uin":    return "application/x-icq";
            case ".uls":    return "text/iuls";
            case ".vcf":    return "text/x-vcard";
            case ".vda":    return "application/x-vda";
            case ".vdx":    return "application/vnd.visio";
            case ".vml":    return "text/xml";
            case ".vpg":    return "application/x-vpeg005";
            case ".vsd":    return "application/x-vsd";
            case ".vss":    return "application/vnd.visio";
            case ".vst":    return "application/x-vst";
            case ".vsw":    return "application/vnd.visio";
            case ".vsx":    return "application/vnd.visio";
            case ".vtx":    return "application/vnd.visio";
            case ".vxml":   return "text/xml";
            case ".wav":    return "audio/wav";
            case ".wax":    return "audio/x-ms-wax";
            case ".wb1":    return "application/x-wb1";
            case ".wb2":    return "application/x-wb2";
            case ".wb3":    return "application/x-wb3";
            case ".wbmp":   return "image/vnd.wap.wbmp";
            case ".wiz":    return "application/msword";
            case ".wk3":    return "application/x-wk3";
            case ".wk4":    return "application/x-wk4";
            case ".wkq":    return "application/x-wkq";
            case ".wks":    return "application/x-wks";
            case ".wm":     return "video/x-ms-wm";
            case ".wma":    return "audio/x-ms-wma";
            case ".wmd":    return "application/x-ms-wmd";
            case ".wmf":    return "application/x-wmf";
            case ".wml":    return "text/vnd.wap.wml";
            case ".wmv":    return "video/x-ms-wmv";
            case ".wmx":    return "video/x-ms-wmx";
            case ".wmz":    return "application/x-ms-wmz";
            case ".wp6":    return "application/x-wp6";
            case ".wpd":    return "application/x-wpd";
            case ".wpg":    return "application/x-wpg";
            case ".wpl":    return "application/vnd.ms-wpl";
            case ".wq1":    return "application/x-wq1";
            case ".wr1":    return "application/x-wr1";
            case ".wri":    return "application/x-wri";
            case ".wrk":    return "application/x-wrk";
            case ".ws":     return "application/x-ws";
            case ".ws2":    return "application/x-ws";
            case ".wsc":    return "text/scriptlet";
            case ".wsdl":   return "text/xml";
            case ".wvx":    return "video/x-ms-wvx";
            case ".xdp":    return "application/vnd.adobe.xdp";
            case ".xdr":    return "text/xml";
            case ".xfd":    return "application/vnd.adobe.xfd";
            case ".xfdf":   return "application/vnd.adobe.xfdf";
            case ".xhtml":  return "text/html";
            case ".xls":    return "application/x-xls";
            case ".xlw":    return "application/x-xlw";
            case ".xml":    return "text/xml";
            case ".xpl":    return "audio/scpls";
            case ".xq":     return "text/xml";
            case ".xql":    return "text/xml";
            case ".xquery": return "text/xml";
            case ".xsd":    return "text/xml";
            case ".xsl":    return "text/xml";
            case ".xslt":   return "text/xml";
            case ".xwd":    return "application/x-xwd";
            case ".x_b":    return "application/x-x_b";
            case ".sis":    return "application/vnd.symbian.install";
            case ".sisx":   return "application/vnd.symbian.install";
            case ".x_t":    return "application/x-x_t";
            case ".ipa":    return "application/vnd.iphone";
            case ".apk":    return "application/vnd.android.package-archive";
            case ".xap":    return "application/x-silverlight-app";
            default:        return defaultType;
        }
    }

}
