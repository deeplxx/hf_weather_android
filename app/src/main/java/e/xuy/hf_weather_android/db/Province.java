package e.xuy.hf_weather_android.db;

import org.litepal.crud.DataSupport;

/**
 * Created by sumlo on 2017/11/20.
 *
 */

public class Province extends DataSupport {

    private int id;
    private String provinceName;
    private int provinceCode;

    public int getId() { return id;}

    public void setId(int id) { this.id = id;}

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String name) {
        this.provinceName = name;
    }

    public int getProvinceCode() {
        return provinceCode;
    }

    public void setProvinceCode(int code) {
        this.provinceCode = code;
    }
}
