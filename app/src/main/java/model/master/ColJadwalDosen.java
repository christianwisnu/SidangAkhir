package model.master;

public class ColJadwalDosen {

    private String nikDosen;
    private String namaDosen;
    private Integer line;
    private String hari;
    private String jamAwal;
    private String jamAkhir;

    public String getNikDosen() {
        return nikDosen;
    }

    public void setNikDosen(String nikDosen) {
        this.nikDosen = nikDosen;
    }

    public String getNamaDosen() {
        return namaDosen;
    }

    public void setNamaDosen(String namaDosen) {
        this.namaDosen = namaDosen;
    }

    public Integer getLine() {
        return line;
    }

    public void setLine(Integer line) {
        this.line = line;
    }

    public String getHari() {
        return hari;
    }

    public void setHari(String hari) {
        this.hari = hari;
    }

    public String getJamAwal() {
        return jamAwal;
    }

    public void setJamAwal(String jamAwal) {
        this.jamAwal = jamAwal;
    }

    public String getJamAkhir() {
        return jamAkhir;
    }

    public void setJamAkhir(String jamAkhir) {
        this.jamAkhir = jamAkhir;
    }
}
