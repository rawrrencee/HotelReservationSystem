/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 *
 * @author Lawrence
 */
@Entity
public class ExceptionReport implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long exReportId;
    
    private String exReportBody;

    public ExceptionReport() {
    }

    public ExceptionReport(String exReportBody) {
        this.exReportBody = exReportBody;
    }

    public Long getExReportId() {
        return exReportId;
    }

    public void setExReportId(Long exReportId) {
        this.exReportId = exReportId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (exReportId != null ? exReportId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the exReportId fields are not set
        if (!(object instanceof ExceptionReport)) {
            return false;
        }
        ExceptionReport other = (ExceptionReport) object;
        if ((this.exReportId == null && other.exReportId != null) || (this.exReportId != null && !this.exReportId.equals(other.exReportId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.ExceptionReport[ id=" + exReportId + " ]";
    }

    /**
     * @return the exReportBody
     */
    public String getExReportBody() {
        return exReportBody;
    }

    /**
     * @param exReportBody the exReportBody to set
     */
    public void setExReportBody(String exReportBody) {
        this.exReportBody = exReportBody;
    }
    
}
