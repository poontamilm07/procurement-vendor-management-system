package com.procurement.procurement.service.procurement;

import com.procurement.procurement.entity.procurement.PurchaseOrder;
import com.procurement.procurement.entity.procurement.PurchaseOrderItem;
import com.procurement.procurement.entity.vendor.Vendor;
import com.procurement.procurement.repository.procurement.PurchaseOrderRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PurchaseOrderService {

    private final PurchaseOrderRepository purchaseOrderRepository;

    public PurchaseOrderService(PurchaseOrderRepository purchaseOrderRepository) {
        this.purchaseOrderRepository = purchaseOrderRepository;
    }

    // ===================== Create Purchase Order =====================
    public PurchaseOrder createPurchaseOrder(PurchaseOrder purchaseOrder) {

        if (purchaseOrder.getItems() != null) {
            purchaseOrder.getItems()
                    .forEach(item -> item.setPurchaseOrder(purchaseOrder));
        }

        return purchaseOrderRepository.save(purchaseOrder);
    }

    // ===================== Update Purchase Order =====================
    public PurchaseOrder updatePurchaseOrder(Long id, PurchaseOrder updatedPO) {

        PurchaseOrder existingPO = purchaseOrderRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Purchase Order not found with id: " + id));

        if (updatedPO.getVendor() != null) {
            existingPO.setVendor(updatedPO.getVendor());
        }

        if (updatedPO.getStatus() != null) {
            existingPO.setStatus(updatedPO.getStatus());
        }

        if (updatedPO.getItems() != null) {
            existingPO.getItems().clear();

            for (PurchaseOrderItem item : updatedPO.getItems()) {
                item.setPurchaseOrder(existingPO);
                existingPO.getItems().add(item);
            }
        }

        return purchaseOrderRepository.save(existingPO);
    }

    // ===================== Get All =====================
    public List<PurchaseOrder> getAllPurchaseOrders() {
        return purchaseOrderRepository.findAll();
    }

    // ===================== Get By ID =====================
    public PurchaseOrder getPurchaseOrderById(Long id) {
        return purchaseOrderRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Purchase Order not found with id: " + id));
    }

    // ===================== Delete =====================
    public void deletePurchaseOrder(Long id) {
        PurchaseOrder po = getPurchaseOrderById(id);
        purchaseOrderRepository.delete(po);
    }
}
