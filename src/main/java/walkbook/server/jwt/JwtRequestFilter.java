package walkbook.server.jwt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Component
public class JwtRequestFilter extends GenericFilterBean {
    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Override
    public void doFilter(ServletRequest request,
                            ServletResponse response,
                            FilterChain filterChain)  throws ServletException, IOException {
        String token = jwtTokenUtil.resolveToken((HttpServletRequest) request);
        if (token != null && jwtTokenUtil.validateToken(token)) {
            Authentication authentication = jwtTokenUtil.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }
}