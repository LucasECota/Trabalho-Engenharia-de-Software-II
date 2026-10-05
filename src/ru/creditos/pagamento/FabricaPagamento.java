package ru.creditos.pagamento;

/** Único lugar que conhece os adaptadores concretos: recebe o nome do gateway e devolve o contrato. */
public final class FabricaPagamento {
    private FabricaPagamento() { }

    public static Pagamento criar(String gateway) {
        return switch (gateway) {
            case "pagapix" -> new PagaPixAdapter();
            case "cobrafacil" -> new CobraFacilAdapter();
            default -> throw new IllegalArgumentException("gateway desconhecido: " + gateway);
        };
    }
}
