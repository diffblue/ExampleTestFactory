//package org.example;
//
//package money.evergreen.backend.service.investment.recommendation;
//
//import static money.evergreen.backend.service.investment.model.instrument.InvestmentCashEquivalentCategory.TRIPLE_TAX_FREE_MUNICIPAL;
//import static money.evergreen.backend.service.investment.recommendation.InvestmentRecommendationCalculator.calculateRiskScore;
//import static money.evergreen.backend.service.investment.recommendation.InvestmentRecommendationUtil.*;
//
//import java.math.BigDecimal;
//import java.util.Set;
//import java.util.stream.Collectors;
//import java.util.stream.Stream;
//
//import com.diffblue.cover.annotations.InTestsMock;
//import com.diffblue.cover.annotations.MockDecision;
//import money.evergreen.backend.lib.common.model.AddressRegion;
//import money.evergreen.backend.lib.util.BigDecimalUtil;
//import money.evergreen.backend.service.investment.model.error.InvalidCashEquivalentTypeSelectionException;
//import money.evergreen.backend.service.investment.model.error.PercentSumMismatchException;
//import money.evergreen.backend.service.investment.model.error.SelectionOutOfRecommendationRangeException;
//import money.evergreen.backend.service.investment.model.instrument.InvestmentCashEquivalentCategory;
//import money.evergreen.backend.service.investment.model.recommendation.*;
//import money.evergreen.backend.service.investment.model.user.request.UpdateFixedIncomeCategoryAllocationsRequest;
//import money.evergreen.backend.service.investment.repo.InvestmentRepo;
//import money.evergreen.backend.service.investment.repo.entity.allocation.EquitySectorEntity;
//import money.evergreen.backend.service.investment.repo.entity.allocation.FixedIncomeAllocationEntity;
//import money.evergreen.backend.service.investment.tax.InvestmentTaxApi;
//import money.evergreen.backend.service.investment.tax.InvestmentTaxCalculator;
//import money.evergreen.backend.service.l1.config.ConfigServiceApi;
//import money.evergreen.backend.service.l1.config.api.response.TripleTaxFreeInstrumentConfig;
//
//@InTestsMock(
//        value = ConfigServiceApi.class,
//        method = "investment",
//        returnValueFactory = "money.evergreen.backend.service.l1.config.impl.MockInvestmentConfigImpl.Instance",
//        decision = MockDecision.REQUIRED
//)
//@InTestsMock(InvestmentRepo.class)
//@InTestsMock(InvestmentTaxApi.class)
//public class InvestmentRecommendationImpl implements InvestmentRecommendationApi {
//    private final InvestmentRepo investmentRepo;
//    private final ConfigServiceApi configService;
//    private final InvestmentTaxApi investmentTaxApi;
//
//    public InvestmentRecommendationImpl(
//            InvestmentRepo investmentRepo, ConfigServiceApi configService, InvestmentTaxApi investmentTaxApi) {
//        this.investmentRepo = investmentRepo;
//        this.configService = configService;
//        this.investmentTaxApi = investmentTaxApi;
//    }
//
//    @Override
//    public RiskToleranceRecommendationResponse getRiskToleranceRecommendation(
//            RiskToleranceRecommendationRequest request) {
//        var riskScore = getRiskScore(request);
//        var recommendedRiskTolerance = mapRiskTolerance(riskScore.value());
//
//        var assetAllocations = investmentRepo.riskTolerance().getLatestAssetAllocations();
//
//        return ImmutableRiskToleranceRecommendationResponse.builder()
//                .orderedRiskTolerances(ORDERED_RISK_TOLERANCES)
//                .recommended(recommendedRiskTolerance)
//                .allowedRiskTolerances(getAllowedListOfRiskTolerances(recommendedRiskTolerance))
//                .assetAllocations(assetAllocations)
//                .build();
//    }
//
//    @Override
//    public RiskScore getRiskScore(RiskToleranceRecommendationRequest request) {
//        BigDecimal federalTaxRate =
//                investmentTaxApi.getFederalTaxRate(request.taxFilingStatus(), request.individualIncome());
//        BigDecimal regionalTaxRate = investmentTaxApi.getRegionalIncomeTaxRate(
//                request.taxFilingStatus(), request.individualIncome(), request.address());
//
//        BigDecimal marginalTaxRate = federalTaxRate.add(regionalTaxRate);
//
//        int calculatedScore =
//                calculateRiskScore(request.age(), request.individualIncome(), request.netWorth(), marginalTaxRate);
//        return RiskScore.of(calculatedScore);
//    }
//
//    @Override
//    public Boolean isValidRiskToleranceSelection(ValidateRiskToleranceRequest request) {
//        var recommendation = getRiskToleranceRecommendation(request.recommendationRequest());
//        return recommendation.allowedRiskTolerances().contains(request.riskTolerance());
//    }
//
//    private Set<AddressRegion> getTripleTaxFreeAddressRegions() {
//        var tripleTaxInstrumentConfig = configService.investment().getLatestTripleTaxFreeInstrumentConfig();
//
//        return tripleTaxInstrumentConfig.stream()
//                .map(TripleTaxFreeInstrumentConfig::addressRegion)
//                .collect(Collectors.toSet());
//    }
//
//    @Override
//    public CashEquivalentRecommendationResponse getCashEquivalentRecommendation(
//            CashEquivalentRecommendationRequest request) {
//        var regionalTaxRate = investmentTaxApi.getRegionalIncomeTaxRate(
//                request.taxFilingStatus(), request.householdIncome(), request.address());
//        var federalTaxRate = investmentTaxApi.getFederalTaxRate(request.taxFilingStatus(), request.householdIncome());
//        var netInvestmentTaxRate =
//                investmentTaxApi.getFederalTaxRate(request.taxFilingStatus(), request.householdIncome());
//
//        var totalTaxRate = regionalTaxRate.add(federalTaxRate).add(netInvestmentTaxRate);
//
//        // 1. calculate tax equivalency fo each type
//        var corporateInstrumentTaxEquivalency = getCorporateInstrumentTaxEquivalency(totalTaxRate);
//        var nationalInstrumentTaxEquivalency = getNationalInstrumentTaxEquivalency(regionalTaxRate);
//        var treasuryBillInstrumentTaxEquivalency =
//                geTreasuryBillInstrumentTaxEquivalency(federalTaxRate.add(netInvestmentTaxRate));
//
//        var tripleTaxFreeAddressRegions = getTripleTaxFreeAddressRegions();
//        var addressRegion = AddressRegion.fromCode(request.address().region());
//
//        // 2 a. if user lives in triple-tax-free regions compare all tax equivalencies & recommend highest
//        if (tripleTaxFreeAddressRegions.contains(addressRegion)) {
//            var tripleTaxInstrumentYield = configService.investment().getLatestTripleTaxInstrumentYield(addressRegion);
//
//            var recommendedCashEquivalent = getRecommendedCashEquivalent(
//                    corporateInstrumentTaxEquivalency,
//                    nationalInstrumentTaxEquivalency,
//                    treasuryBillInstrumentTaxEquivalency,
//                    tripleTaxInstrumentYield);
//
//            return ImmutableCashEquivalentRecommendationResponse.builder()
//                    .recommended(recommendedCashEquivalent)
//                    .allowTripleTaxFreeMunicipalMutualFund(true)
//                    .build();
//        }
//
//        // 2 b. else just compare corporate, national & treasury bill instrument tax equivalencies & recommend highest
//        var recommendedCashEquivalent = getRecommendedCashEquivalent(
//                corporateInstrumentTaxEquivalency,
//                nationalInstrumentTaxEquivalency,
//                treasuryBillInstrumentTaxEquivalency,
//                BigDecimal.ZERO);
//        return ImmutableCashEquivalentRecommendationResponse.builder()
//                .recommended(recommendedCashEquivalent)
//                .allowTripleTaxFreeMunicipalMutualFund(false)
//                .build();
//    }
//
//    private BigDecimal getCorporateInstrumentTaxEquivalency(BigDecimal effectiveTaxRate) {
//        var yield =
//                configService.investment().getCorporateMoneyMarketInstrument().instrumentYield();
//        yield = yield.add(effectiveTaxRate);
//        return InvestmentTaxCalculator.calculateTaxEquivalency(effectiveTaxRate, yield);
//    }
//
//    private BigDecimal getNationalInstrumentTaxEquivalency(BigDecimal effectiveTaxRate) {
//        var yield =
//                configService.investment().getNationalMoneyMarketInstrument().instrumentYield();
//        return InvestmentTaxCalculator.calculateTaxEquivalency(effectiveTaxRate, yield);
//    }
//
//    private BigDecimal geTreasuryBillInstrumentTaxEquivalency(BigDecimal effectiveTaxRate) {
//        var yield = configService
//                .investment()
//                .getTreasuryBillMoneyMarketInstrument()
//                .instrumentYield();
//        return InvestmentTaxCalculator.calculateTaxEquivalency(effectiveTaxRate, yield);
//    }
//
//    @Override
//    public EquitySectorAllocationRecommendationResponse getEquitySectorRecommendation() {
//        var config = configService.investment().getEquitySectorRecommendation();
//        return toEquitySectorRecommendationResponse(config);
//    }
//
//    @Override
//    public FixedIncomeCategoryAllocationRecommendationResponse getFixedIncomeRecommendation(
//            FixedIncomeCategoryAllocationRecommendationRequest request) {
//        var federalTaxRate = investmentTaxApi.getFederalTaxRate(request.taxFilingStatus(), request.individualIncome());
//        var regionalTaxRate = investmentTaxApi.getRegionalIncomeTaxRate(
//                request.taxFilingStatus(), request.individualIncome(), request.address());
//        var marginalTaxRate = federalTaxRate.add(regionalTaxRate);
//        int riskScore =
//                calculateRiskScore(request.age(), request.individualIncome(), request.netWorth(), marginalTaxRate);
//
//        var config = configService.investment().getFixedIncomeCategoryRecommendation(BigDecimal.valueOf(riskScore));
//        return toFixedIncomeCategoryRecommendationResponse(config);
//    }
//
//    @Override
//    public void validateRiskTolerance(ValidateRiskToleranceRequest request)
//            throws SelectionOutOfRecommendationRangeException {
//        if (isValidRiskToleranceSelection(request)) {
//            return;
//        }
//        throw new SelectionOutOfRecommendationRangeException();
//    }
//
//    @Override
//    public void validateEquitySectorAllocations(EquitySectorEntity equitySectorEntity, boolean validateRecommendations)
//            throws PercentSumMismatchException, SelectionOutOfRecommendationRangeException {
//        validateAllocationSum(equitySectorEntity);
//        if (validateRecommendations) {
//            validateAllocationSelectionRange(equitySectorEntity);
//        }
//    }
//
//    @Override
//    public void validateFixedIncomeCategoryAllocations(
//            UpdateFixedIncomeCategoryAllocationsRequest request, boolean validateRecommendations)
//            throws PercentSumMismatchException, SelectionOutOfRecommendationRangeException {
//        validateAllocationSum(request.updateEntity());
//        if (validateRecommendations) {
//            validateAllocationSelectionRange(request);
//        }
//    }
//
//    @Override
//    public void validateCashEquivalentType(
//            AddressRegion addressRegion, InvestmentCashEquivalentCategory investmentCashEquivalentCategory)
//            throws InvalidCashEquivalentTypeSelectionException {
//
//        if (!investmentCashEquivalentCategory.equals(TRIPLE_TAX_FREE_MUNICIPAL)) {
//            return;
//        }
//
//        var configuredAddressRegions = getTripleTaxFreeAddressRegions();
//        if (configuredAddressRegions.contains(addressRegion)) {
//            return;
//        }
//
//        throw new InvalidCashEquivalentTypeSelectionException();
//    }
//
//    @Override
//    public void updateRiskToleranceAssetAllocationConfig(UpdateRiskToleranceAssetAllocationRequest request) {
//        investmentRepo.riskTolerance().updateAssetAllocationConfig(request);
//    }
//
//    private void validateAllocationSelectionRange(EquitySectorEntity equitySectorEntity)
//            throws SelectionOutOfRecommendationRangeException {
//        var recommendation = getEquitySectorRecommendation();
//
//        if (!BigDecimalUtil.isInclusiveBetween(
//                equitySectorEntity.healthcare(),
//                recommendation.healthcare().min(),
//                recommendation.healthcare().max())) {
//            throw new SelectionOutOfRecommendationRangeException();
//        }
//        if (!BigDecimalUtil.isInclusiveBetween(
//                equitySectorEntity.consumer(),
//                recommendation.consumer().min(),
//                recommendation.consumer().max())) {
//            throw new SelectionOutOfRecommendationRangeException();
//        }
//        if (!BigDecimalUtil.isInclusiveBetween(
//                equitySectorEntity.financial(),
//                recommendation.financial().min(),
//                recommendation.financial().max())) {
//            throw new SelectionOutOfRecommendationRangeException();
//        }
//        if (!BigDecimalUtil.isInclusiveBetween(
//                equitySectorEntity.technology(),
//                recommendation.technology().min(),
//                recommendation.technology().max())) {
//            throw new SelectionOutOfRecommendationRangeException();
//        }
//        if (!BigDecimalUtil.isInclusiveBetween(
//                equitySectorEntity.industry(),
//                recommendation.industry().min(),
//                recommendation.industry().max())) {
//            throw new SelectionOutOfRecommendationRangeException();
//        }
//        if (!BigDecimalUtil.isInclusiveBetween(
//                equitySectorEntity.energy(),
//                recommendation.energy().min(),
//                recommendation.energy().max())) {
//            throw new SelectionOutOfRecommendationRangeException();
//        }
//        if (!BigDecimalUtil.isInclusiveBetween(
//                equitySectorEntity.realEstate(),
//                recommendation.realEstate().min(),
//                recommendation.realEstate().max())) {
//            throw new SelectionOutOfRecommendationRangeException();
//        }
//    }
//
//    private void validateAllocationSelectionRange(UpdateFixedIncomeCategoryAllocationsRequest request)
//            throws SelectionOutOfRecommendationRangeException {
//        var recommendation = getFixedIncomeRecommendation(toFixedIncomeAllocationRecommendationRequest(request));
//
//        if (!BigDecimalUtil.isInclusiveBetween(
//                request.updateEntity().governmentBond(),
//                recommendation.governmentBond().min(),
//                recommendation.governmentBond().max())) {
//            throw new SelectionOutOfRecommendationRangeException();
//        }
//        if (!BigDecimalUtil.isInclusiveBetween(
//                request.updateEntity().highYieldBond(),
//                recommendation.highYieldBond().min(),
//                recommendation.highYieldBond().max())) {
//            throw new SelectionOutOfRecommendationRangeException();
//        }
//        if (!BigDecimalUtil.isInclusiveBetween(
//                request.updateEntity().corporateBond(),
//                recommendation.corporateBond().min(),
//                recommendation.corporateBond().max())) {
//            throw new SelectionOutOfRecommendationRangeException();
//        }
//        if (!BigDecimalUtil.isInclusiveBetween(
//                request.updateEntity().municipalBond(),
//                recommendation.municipalBond().min(),
//                recommendation.municipalBond().max())) {
//            throw new SelectionOutOfRecommendationRangeException();
//        }
//        if (!BigDecimalUtil.isInclusiveBetween(
//                request.updateEntity().internationalBond(),
//                recommendation.internationalBond().min(),
//                recommendation.internationalBond().max())) {
//            throw new SelectionOutOfRecommendationRangeException();
//        }
//    }
//
//    private FixedIncomeCategoryAllocationRecommendationRequest toFixedIncomeAllocationRecommendationRequest(
//            UpdateFixedIncomeCategoryAllocationsRequest request) {
//        return ImmutableFixedIncomeCategoryAllocationRecommendationRequest.builder()
//                .age(request.age())
//                .individualIncome(request.individualIncome())
//                .netWorth(request.netWorth())
//                .address(request.address())
//                .taxFilingStatus(request.taxFilingStatus())
//                .build();
//    }
//
//    private static void validateAllocationSum(EquitySectorEntity equitySectorEntity)
//            throws PercentSumMismatchException {
//        var sum = Stream.of(
//                        equitySectorEntity.healthcare(),
//                        equitySectorEntity.consumer(),
//                        equitySectorEntity.financial(),
//                        equitySectorEntity.technology(),
//                        equitySectorEntity.industry(),
//                        equitySectorEntity.energy(),
//                        equitySectorEntity.realEstate(),
//                        equitySectorEntity.media())
//                .reduce(BigDecimal.ZERO, BigDecimal::add);
//
//        if (BigDecimalUtil.equalTo(sum, BigDecimal.ONE)) {
//            return;
//        }
//
//        throw new PercentSumMismatchException();
//    }
//
//    private static void validateAllocationSum(FixedIncomeAllocationEntity fixedIncomeAllocationEntity)
//            throws PercentSumMismatchException {
//        var sum = Stream.of(
//                        fixedIncomeAllocationEntity.governmentBond(),
//                        fixedIncomeAllocationEntity.highYieldBond(),
//                        fixedIncomeAllocationEntity.corporateBond(),
//                        fixedIncomeAllocationEntity.municipalBond(),
//                        fixedIncomeAllocationEntity.internationalBond())
//                .reduce(BigDecimal.ZERO, BigDecimal::add);
//
//        if (BigDecimalUtil.equalTo(sum, BigDecimal.ONE)) {
//            return;
//        }
//
//        throw new PercentSumMismatchException();
//    }
//}
