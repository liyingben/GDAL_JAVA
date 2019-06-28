import org.gdal.gdal.Dataset;
import org.gdal.gdal.gdal;
import org.gdal.gdalconst.gdalconstConstants;

public class GeoInfo {

    /// <summary>
    /// 图像宽度
    /// </summary>
    public int RasterXSize;
    /// <summary>
    /// 图像高度
    /// </summary>
    public int RasterYSize;
    /// <summary>
    /// 地理参考
    /// </summary>
    public String ProjectionRef;
    /// <summary>
    /// GCP描述
    /// </summary>
    public String GCPProjection;
    /// <summary>
    /// geo数组
    /// </summary>
    public double[] GeoTransform;
    /// <summary>
    /// GCP列表
    /// </summary>
    //public GCP[] GCPList;
    /// <summary>
    /// 默认元数据
    /// </summary>
    public String DefaultMetaData;
    /// <summary>
    ///IMAGE_STRUCTURE元数据
    /// </summary>
    public String ImageStructureMetaData;

    /// <summary>
    ///SUBDATASETS元数据
    /// </summary>
    public String SubdatasetsMetaData;
    /// <summary>
    ///GEOLOCATION元数据
    /// </summary>
    public String GeolocationMetaData;

    public void getInfo(String path) {
        gdal.AllRegister();
        Dataset hDataset = gdal.Open(path, gdalconstConstants.GA_ReadOnly);
        RasterXSize = hDataset.getRasterXSize();
        RasterYSize = hDataset.getRasterYSize();
        ProjectionRef = hDataset.GetProjectionRef().toString();
        GCPProjection = hDataset.GetGCPProjection();
        double[] geoTransform = new double[6];
        hDataset.GetGeoTransform(geoTransform);
        GeoTransform = geoTransform;
        DefaultMetaData = hDataset.GetMetadataItem("");//默认元数据
        ImageStructureMetaData = hDataset.GetMetadataItem("IMAGE_STRUCTURE");//IMAGE_STRUCTURE元数据
        SubdatasetsMetaData = hDataset.GetMetadataItem("SUBDATASETS");//SUBDATASETS元数据
        GeolocationMetaData = hDataset.GetMetadataItem("GEOLOCATION");//GEOLOCATION元数据

        hDataset.delete();
    }
}
