package br.com.megalaser.dwgpdf;

/**
 * Created by rodrigo on 09/02/18.
 */

public class PartsInfo {

    private String refCod;
    private String refCodClient;
    private String revision;
    private String denomination;
    private String typeMaterial;
    private String nameClient;

    public String getRefCod() {
        return refCod;
    }

    public void setRefCod(String refCod) {
        this.refCod = refCod;
    }

    public String getRefCodClient() {
        return refCodClient;
    }

    public void setRefCodClient(String refCodClient) {
        this.refCodClient = refCodClient;
    }

    public String getRevision() {
        return revision;
    }

    public void setRevision(String revision) {
        this.revision = revision;
    }

    public String getDenomination() {
        return denomination;
    }

    public void setDenomination(String denomination) {
        this.denomination = denomination;
    }

    public String getTypeMaterial() {
        return typeMaterial;
    }

    public void setTypeMaterial(String typeMaterial) {
        this.typeMaterial = typeMaterial;
    }

    public String getNameClient() {
        return nameClient;
    }

    public void setNameClient(String nameClient) {
        this.nameClient = nameClient;
    }
}
