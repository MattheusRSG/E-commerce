package com.ecommerce.config;

import com.ecommerce.entity.Categoria;
import com.ecommerce.entity.Produto;
import com.ecommerce.entity.Usuario;
import com.ecommerce.repository.CategoriaRepository;
import com.ecommerce.repository.ProdutoRepository;
import com.ecommerce.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@Component
@Order(1)
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private CategoriaRepository categoriaRepository;
    
    @Autowired
    private ProdutoRepository produtoRepository;
    
    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public void run(String... args) throws Exception {
        if (categoriaRepository.count() == 0) {
            criarCategorias();
        }
        
        if (produtoRepository.count() == 0) {
            criarProdutos();
        }
        
        criarUsuarios();
    }

    private void criarCategorias() {
        List<Categoria> categorias = Arrays.asList(
            new Categoria("Vestuário", "Roupas femininas para todas as ocasiões"),
            new Categoria("Moda Fitness", "Roupas esportivas e fitness feminino"),
            new Categoria("Moda Íntima", "Lingerie e moda íntima feminina"),
            new Categoria("Calçados", "Sapatos, sandálias e tênis femininos"),
            new Categoria("Acessórios", "Bolsas, joias e acessórios femininos"),
            new Categoria("Maquiagem", "Cosméticos e produtos de beleza feminina")
        );
        
        categoriaRepository.saveAll(categorias);
        System.out.println("✅ " + categorias.size() + " categorias criadas");
    }

    private void criarProdutos() {
        List<Categoria> categorias = categoriaRepository.findAll();
        
        // Vestuário
        Categoria vestuario = categorias.stream().filter(c -> c.getNome().equals("Vestuário")).findFirst().orElse(null);
        if (vestuario != null) {
            Produto blusa = new Produto("Blusa Feminina Básica", "Blusa básica em algodão, confortável e versátil", 
                new BigDecimal("49.90"), 50, vestuario);
            blusa.setEstoqueMinimo(5);
            blusa.setTamanhos("P,M,G,GG");
            blusa.setCores("Branco,Preto,Rosa,Azul");
            blusa.setUrlImagem("https://images.unsplash.com/photo-1594633312681-425c7b97ccd1?w=300&h=300&fit=crop");
            
            Produto vestido = new Produto("Vestido Floral Midi", "Vestido midi com estampa floral, perfeito para o verão", 
                new BigDecimal("89.90"), 30, vestuario);
            vestido.setEstoqueMinimo(3);
            vestido.setTamanhos("P,M,G,GG");
            vestido.setCores("Floral Rosa,Floral Azul");
            vestido.setUrlImagem("https://images.unsplash.com/photo-1572804013309-59a88b7e92f1?w=300&h=300&fit=crop");
            
            Produto calca = new Produto("Calça Jeans Skinny", "Calça jeans skinny com elastano, modelagem perfeita", 
                new BigDecimal("79.90"), 40, vestuario);
            calca.setEstoqueMinimo(5);
            calca.setTamanhos("36,38,40,42,44");
            calca.setCores("Azul Claro,Azul Escuro,Preto");
            calca.setUrlImagem("https://images.unsplash.com/photo-1541099649105-f69ad21f3246?w=300&h=300&fit=crop");
            
            produtoRepository.saveAll(Arrays.asList(blusa, vestido, calca));
        }

        // Moda Fitness
        Categoria fitness = categorias.stream().filter(c -> c.getNome().equals("Moda Fitness")).findFirst().orElse(null);
        if (fitness != null) {
            Produto top = new Produto("Top Fitness com Bojo", "Top esportivo com bojo removível e tecido que seca rápido", 
                new BigDecimal("39.90"), 60, fitness);
            top.setEstoqueMinimo(10);
            top.setTamanhos("P,M,G,GG");
            top.setCores("Preto,Rosa,Roxo,Verde");
            top.setUrlImagem("https://images.unsplash.com/photo-1506629905607-d9c297d3d45b?w=300&h=300&fit=crop");
            
            Produto legging = new Produto("Legging Fitness Estampada", "Legging com estampa exclusiva e tecido anti-suor", 
                new BigDecimal("59.90"), 45, fitness);
            legging.setEstoqueMinimo(8);
            legging.setTamanhos("P,M,G,GG");
            legging.setCores("Estampa Floral,Estampa Geométrica,Lisa Preta");
            legging.setUrlImagem("https://images.unsplash.com/photo-1571019613454-1cb2f99b2d8b?w=300&h=300&fit=crop");
            
            Produto shorts = new Produto("Shorts Fitness Cintura Alta", "Shorts esportivo com cintura alta e tecido respirável", 
                new BigDecimal("34.90"), 55, fitness);
            shorts.setEstoqueMinimo(10);
            shorts.setTamanhos("P,M,G,GG");
            shorts.setCores("Preto,Cinza,Rosa,Azul");
            shorts.setUrlImagem("https://images.unsplash.com/photo-1544966503-7cc5ac882d5f?w=300&h=300&fit=crop");
            
            produtoRepository.saveAll(Arrays.asList(top, legging, shorts));
        }

        // Calçados
        Categoria calcados = categorias.stream().filter(c -> c.getNome().equals("Calçados")).findFirst().orElse(null);
        if (calcados != null) {
            Produto tenis = new Produto("Tênis Casual Feminino", "Tênis confortável para o dia a dia", 
                new BigDecimal("129.90"), 35, calcados);
            tenis.setEstoqueMinimo(5);
            tenis.setTamanhos("35,36,37,38,39,40");
            tenis.setCores("Branco,Preto,Rosa");
            tenis.setUrlImagem("https://images.unsplash.com/photo-1549298916-b41d501d3772?w=300&h=300&fit=crop");
            
            Produto sandalia = new Produto("Sandália de Salto Alto", "Sandália elegante com salto alto para ocasiões especiais", 
                new BigDecimal("99.90"), 20, calcados);
            sandalia.setEstoqueMinimo(2);
            sandalia.setTamanhos("35,36,37,38,39,40");
            sandalia.setCores("Nude,Preto,Vermelho");
            sandalia.setUrlImagem("https://images.unsplash.com/photo-1543163521-1bf539c55dd2?w=300&h=300&fit=crop");
            
            produtoRepository.saveAll(Arrays.asList(tenis, sandalia));
        }

        // Acessórios
        Categoria acessorios = categorias.stream().filter(c -> c.getNome().equals("Acessórios")).findFirst().orElse(null);
        if (acessorios != null) {
            Produto bolsa = new Produto("Bolsa Feminina Grande", "Bolsa espaçosa para o dia a dia", 
                new BigDecimal("79.90"), 25, acessorios);
            bolsa.setEstoqueMinimo(3);
            bolsa.setTamanhos("Único");
            bolsa.setCores("Camel,Preto,Marrom");
            bolsa.setUrlImagem("https://images.unsplash.com/photo-1553062407-98eeb64c6a62?w=300&h=300&fit=crop");
            
            Produto colar = new Produto("Colar Dourado Delicado", "Colar feminino dourado com pingente coração", 
                new BigDecimal("29.90"), 50, acessorios);
            colar.setEstoqueMinimo(10);
            colar.setTamanhos("Único");
            colar.setCores("Dourado,Prateado");
            colar.setUrlImagem("https://images.unsplash.com/photo-1515562141207-7a88fb7ce338?w=300&h=300&fit=crop");
            
            Produto oculos = new Produto("Óculos de Sol Feminino", "Óculos de sol com proteção UV e design moderno", 
                new BigDecimal("89.90"), 30, acessorios);
            oculos.setEstoqueMinimo(5);
            oculos.setTamanhos("Único");
            oculos.setCores("Preto,Marrom,Dourado");
            oculos.setUrlImagem("https://images.unsplash.com/photo-1511499767150-a48a237f0083?w=300&h=300&fit=crop");
            
            Produto relogio = new Produto("Relógio Feminino Elegante", "Relógio com pulseira de couro e mostrador delicado", 
                new BigDecimal("149.90"), 20, acessorios);
            relogio.setEstoqueMinimo(3);
            relogio.setTamanhos("Único");
            relogio.setCores("Dourado,Prateado,Rose Gold");
            relogio.setUrlImagem("https://images.unsplash.com/photo-1524592094714-0f0654e20314?w=300&h=300&fit=crop");
            
            produtoRepository.saveAll(Arrays.asList(bolsa, colar, oculos, relogio));
        }

        // Moda Íntima
        Categoria modaIntima = categorias.stream().filter(c -> c.getNome().equals("Moda Íntima")).findFirst().orElse(null);
        if (modaIntima != null) {
            Produto sutia = new Produto("Sutiã com Bojo Renda", "Sutiã com bojo e detalhes em renda, conforto e elegância", 
                new BigDecimal("39.90"), 45, modaIntima);
            sutia.setEstoqueMinimo(8);
            sutia.setTamanhos("36,38,40,42,44");
            sutia.setCores("Preto,Branco,Nude,Vermelho");
            sutia.setUrlImagem("https://images.unsplash.com/photo-1544441893-675973e31985?w=300&h=300&fit=crop");
            
            Produto calcinha = new Produto("Calcinha Fio Dental Renda", "Calcinha fio dental com renda delicada e confortável", 
                new BigDecimal("19.90"), 60, modaIntima);
            calcinha.setEstoqueMinimo(12);
            calcinha.setTamanhos("P,M,G,GG");
            calcinha.setCores("Preto,Branco,Rosa,Nude");
            calcinha.setUrlImagem("https://images.unsplash.com/photo-1583394838336-acd977736f90?w=300&h=300&fit=crop");
            
            Produto conjunto = new Produto("Conjunto Lingerie Luxo", "Conjunto completo de lingerie com renda francesa", 
                new BigDecimal("79.90"), 25, modaIntima);
            conjunto.setEstoqueMinimo(5);
            conjunto.setTamanhos("P,M,G,GG");
            conjunto.setCores("Preto,Vermelho,Azul Marinho");
            conjunto.setUrlImagem("https://images.unsplash.com/photo-1583394838336-acd977736f90?w=300&h=300&fit=crop");
            
            produtoRepository.saveAll(Arrays.asList(sutia, calcinha, conjunto));
        }

        // Maquiagem
        Categoria maquiagem = categorias.stream().filter(c -> c.getNome().equals("Maquiagem")).findFirst().orElse(null);
        if (maquiagem != null) {
            Produto batom = new Produto("Batom Matte Longa Duração", "Batom com acabamento matte e fórmula que dura até 8 horas", 
                new BigDecimal("24.90"), 80, maquiagem);
            batom.setEstoqueMinimo(15);
            batom.setTamanhos("Único");
            batom.setCores("Vermelho,Rosa,Nude,Vinho");
            batom.setUrlImagem("https://images.unsplash.com/photo-1586495777744-4413f21062fa?w=300&h=300&fit=crop");
            
            Produto base = new Produto("Base Líquida Cobertura Natural", "Base com cobertura natural e proteção solar FPS 15", 
                new BigDecimal("39.90"), 60, maquiagem);
            base.setEstoqueMinimo(10);
            base.setTamanhos("Único");
            base.setCores("Bege Claro,Bege Médio,Bege Escuro,Morena Clara,Morena Escura");
            base.setUrlImagem("https://images.unsplash.com/photo-1596462502278-27bfdc403348?w=300&h=300&fit=crop");
            
            Produto paleta = new Produto("Paleta de Sombras Nude", "Paleta com 12 tons nude para looks naturais e sofisticados", 
                new BigDecimal("49.90"), 40, maquiagem);
            paleta.setEstoqueMinimo(8);
            paleta.setTamanhos("Único");
            paleta.setCores("Tons Nude,Tons Rosé,Tons Marrom");
            paleta.setUrlImagem("https://images.unsplash.com/photo-1512496015851-a90fb38ba796?w=300&h=300&fit=crop");
            
            Produto rimel = new Produto("Rímel à Prova D'água", "Rímel com fórmula resistente à água e efeito volume", 
                new BigDecimal("19.90"), 70, maquiagem);
            rimel.setEstoqueMinimo(15);
            rimel.setTamanhos("Único");
            rimel.setCores("Preto,Marrom");
            rimel.setUrlImagem("https://images.unsplash.com/photo-1583394838336-acd977736f90?w=300&h=300&fit=crop");
            
            produtoRepository.saveAll(Arrays.asList(batom, base, paleta, rimel));
        }

        System.out.println("✅ Produtos criados com sucesso");
    }

    private void criarUsuarios() {
        // Verificar se admin já existe
        if (usuarioRepository.findByLogin("admin").isEmpty()) {
            Usuario admin = new Usuario("Administrador", "admin@estilofeminino.com", "admin", "admin123");
            admin.setTipoUsuario(Usuario.TipoUsuario.ADMIN);
            admin.setTelefone("11999999999");
            admin.setCep("01234-567");
            admin.setCidade("São Paulo");
            admin.setEstado("SP");
            admin.setLogradouro("Rua das Flores");
            admin.setNumero("123");
            admin.setCpf("11111111111");
            usuarioRepository.save(admin);
            System.out.println("✅ Usuário ADMIN criado: admin/admin123");
        }
        
        // Verificar se cliente já existe
        if (usuarioRepository.findByLogin("cliente").isEmpty()) {
            Usuario cliente = new Usuario("Cliente Teste", "cliente@teste.com", "cliente", "123456");
            cliente.setTipoUsuario(Usuario.TipoUsuario.CLIENTE);
            cliente.setTelefone("11888888888");
            cliente.setCep("04567-890");
            cliente.setCidade("São Paulo");
            cliente.setEstado("SP");
            cliente.setLogradouro("Av. Paulista");
            cliente.setNumero("1000");
            cliente.setComplemento("Apto 101");
            cliente.setCpf("22222222222");
            usuarioRepository.save(cliente);
            System.out.println("✅ Usuário CLIENTE criado: cliente/123456");
        }
    }
}
